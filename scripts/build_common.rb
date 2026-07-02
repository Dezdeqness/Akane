# frozen_string_literal: true

# Shared helpers for build_android.rb / build_desktop.rb.

require "rbconfig"
require "pathname"

REPO_ROOT = File.expand_path("..", __dir__)
DIST_DIR = File.join(REPO_ROOT, "dist")

HOST_OS =
  case RbConfig::CONFIG["host_os"]
  when /mswin|mingw|cygwin/ then :windows
  when /darwin/             then :macos
  when /linux/              then :linux
  else :unknown
  end

# Reads a key=value file (app.properties / local.properties) into a hash.
def read_properties(path)
  props = {}
  File.foreach(path) do |line|
    next if line.strip.empty? || line.strip.start_with?("#")

    key, _, value = line.partition("=")
    props[key.strip] = value.strip
  end
  props
end

# Runs Gradle with the wrapper matching the current OS; aborts on non-zero exit.
def gradle(tasks)
  wrapper = HOST_OS == :windows ? File.join(REPO_ROOT, "gradlew.bat") : File.join(REPO_ROOT, "gradlew")
  cmd = [wrapper, *tasks]
  puts "Gradle: #{tasks.join(' ')}"
  ok = system(*cmd, chdir: REPO_ROOT)
  abort "Gradle failed with exit code #{$?.exitstatus}" unless ok
end

# Newest file matching a glob pattern (or nil).
def newest(glob)
  Dir.glob(glob).max_by { |f| File.mtime(f) }
end
