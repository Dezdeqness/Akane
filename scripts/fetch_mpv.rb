#!/usr/bin/env ruby
# frozen_string_literal: true

# Fetches the bundled libmpv-2.dll for the desktop player (Windows) from the
# shinchiro/mpv-winbuild-cmake releases.
# Run it once before building the desktop app on Windows.
#
# Usage:
#   ruby scripts/fetch_mpv.rb
#   ruby scripts/fetch_mpv.rb 20260901   # override the release
#   ruby scripts/fetch_mpv.rb --v3       # x86_64-v3 variant (needs an AVX2 CPU)
#   ruby scripts/fetch_mpv.rb --force    # re-download even if the DLL is present
#
# Set GITHUB_TOKEN to avoid the unauthenticated GitHub API rate limit.

require_relative "build_common"
require "net/http"
$stdout.sync = true
require "json"
require "uri"
require "digest"
require "fileutils"
require "tmpdir"

MPV_REPO = "shinchiro/mpv-winbuild-cmake"
MPV_TAG  = "20260902"

DLL_PATH = File.join(
  REPO_ROOT,
  "features", "videoplayer", "src", "desktopMain", "resources",
  "mpv", "win32-x86-64", "libmpv-2.dll"
)

force = !ARGV.delete("--force").nil?
v3 = !ARGV.delete("--v3").nil?
tag = ARGV.first&.strip
tag = MPV_TAG if tag.nil? || tag.empty?
variant = v3 ? "x86_64-v3" : "x86_64"

def http_get_body(url, headers, limit = 5)
  raise "too many redirects" if limit <= 0

  uri = URI(url)
  res = Net::HTTP.start(uri.host, uri.port, use_ssl: uri.scheme == "https") do |http|
    req = Net::HTTP::Get.new(uri)
    headers.each { |k, v| req[k] = v }
    http.request(req)
  end

  case res
  when Net::HTTPSuccess      then res.body
  when Net::HTTPRedirection  then http_get_body(res["location"], headers, limit - 1)
  else raise "HTTP #{res.code} for #{url}: #{res.body.to_s[0, 200]}"
  end
end

def download(url, dest, headers, limit = 5)
  raise "too many redirects" if limit <= 0

  uri = URI(url)
  Net::HTTP.start(uri.host, uri.port, use_ssl: uri.scheme == "https") do |http|
    req = Net::HTTP::Get.new(uri)
    headers.each { |k, v| req[k] = v }
    http.request(req) do |res|
      case res
      when Net::HTTPSuccess
        total = res.content_length # nil if the server doesn't send Content-Length
        done = 0
        last_pct = -1
        File.open(dest, "wb") do |f|
          res.read_body do |chunk|
            f.write(chunk)
            done += chunk.bytesize
            if total && total.positive?
              pct = done * 100 / total
              if pct != last_pct
                printf("\r  %d%% (%.1f / %.1f MB)", pct, done / 1_048_576.0, total / 1_048_576.0)
                last_pct = pct
              end
            else
              printf("\r  %.1f MB", done / 1_048_576.0)
            end
          end
        end
        print "\n"
        return
      when Net::HTTPRedirection
        return download(res["location"], dest, headers, limit - 1)
      else
        raise "HTTP #{res.code} for #{url}"
      end
    end
  end
end

def extract_member(archive, member, dest)
  tar = File.join(ENV["SystemRoot"] || "C:\\Windows", "System32", "tar.exe")
  tar = "tar" unless File.executable?(tar)

  Dir.mktmpdir do |dir|
    ok = system(tar, "-xf", archive, "-C", dir, member)
    extracted = File.join(dir, member)
    abort "fetch_mpv: failed to extract #{member} from #{File.basename(archive)}" unless ok && File.file?(extracted)

    FileUtils.mkdir_p(File.dirname(dest))
    FileUtils.cp(extracted, dest)
  end
end

unless HOST_OS == :windows
  puts "fetch_mpv: host is not Windows (#{HOST_OS}) — libmpv is Windows-only here, skipping."
  exit 0
end

if File.exist?(DLL_PATH) && !force
  puts "fetch_mpv: libmpv-2.dll already present — skipping (use --force to re-download)."
  exit 0
end

api_headers = {
  "User-Agent" => "akane-fetch-mpv",
  "Accept" => "application/vnd.github+json"
}
token = ENV["GITHUB_TOKEN"]
api_headers["Authorization"] = "Bearer #{token}" if token && !token.empty?

release_url = "https://api.github.com/repos/#{MPV_REPO}/releases/tags/#{tag}"
puts "fetch_mpv: release #{MPV_REPO}@#{tag}, variant #{variant} …"
release = JSON.parse(http_get_body(release_url, api_headers))

asset_re = /\Ampv-dev-#{Regexp.escape(variant)}-\d+-git-[0-9a-f]+\.7z\z/
asset = (release["assets"] || []).find { |a| a["name"] =~ asset_re }
abort "fetch_mpv: no mpv-dev-#{variant}-*.7z asset in release #{tag}" if asset.nil?

Dir.mktmpdir do |dir|
  archive = File.join(dir, asset["name"])
  puts "fetch_mpv: downloading #{asset['name']} …"
  download(asset["browser_download_url"], archive, { "User-Agent" => "akane-fetch-mpv" })

  actual = Digest::SHA256.file(archive).hexdigest
  expected = asset["digest"].to_s.sub(/\Asha256:/, "")
  if expected.empty?
    puts "fetch_mpv: no SHA-256 from the API, skipping the check (downloaded #{actual})."
  elsif actual.casecmp(expected) != 0
    abort "fetch_mpv: SHA-256 mismatch: expected #{expected}, got #{actual}"
  else
    puts "fetch_mpv: SHA-256 ok."
  end

  extract_member(archive, "libmpv-2.dll", DLL_PATH)
end

puts "fetch_mpv: done -> #{DLL_PATH}"
