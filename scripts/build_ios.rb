#!/usr/bin/env ruby
# frozen_string_literal: true

# Builds the iOS app and uploads it to TestFlight via fastlane.
# macOS only — requires Xcode and a Ruby toolchain with fastlane installed
# (run `bundle install` inside akane-ios/ once).
#
# Usage:
#   ruby scripts/build_ios.rb               # archive + upload to TestFlight
#   ruby scripts/build_ios.rb --no-upload   # archive only, copy .ipa into dist/

require "fileutils"
require_relative "build_common"

abort "iOS builds require macOS with Xcode." unless HOST_OS == :macos

upload = !ARGV.include?("--no-upload")

IOS_DIR = File.join(REPO_ROOT, "akane-ios")
PBXPROJ = File.join(IOS_DIR, "iosApp.xcodeproj", "project.pbxproj")

app = read_properties(File.join(REPO_ROOT, "app.properties"))
version = app["app.version"]
abort "app.version missing in app.properties" if version.to_s.strip.empty?
puts "Marketing version: #{version}"

pbx = File.read(PBXPROJ)
synced = pbx.gsub(/MARKETING_VERSION = [^;]+;/, "MARKETING_VERSION = #{version};")
if synced != pbx
  File.write(PBXPROJ, synced)
  puts "Synced MARKETING_VERSION -> #{version} in project.pbxproj"
end

lane = upload ? "buildAndUpload" : "build"
puts "fastlane #{lane}"
ok = system("bundle", "exec", "fastlane", lane, chdir: IOS_DIR)
abort "fastlane failed with exit code #{$?.exitstatus}" unless ok

ipa = newest(File.join(IOS_DIR, "build/fastlane/*.ipa"))
if ipa
  FileUtils.mkdir_p(DIST_DIR)
  out = File.join(DIST_DIR, "Akane-#{version}-release.ipa")
  FileUtils.cp(ipa, out)
  puts "IPA -> #{out}"
else
  warn "No .ipa found in akane-ios/build/fastlane (upload lane skips local export?)."
end

puts "Done."
