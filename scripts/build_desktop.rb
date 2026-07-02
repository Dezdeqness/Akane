#!/usr/bin/env ruby
# frozen_string_literal: true

# Builds the Akane desktop distribution for the CURRENT OS and copies it into dist/.
# Cross-platform: works on Windows and macOS/Linux alike.
#
#   Windows -> .exe + Akane-windows-x64-<ver>.jar
#   macOS   -> .dmg + Akane-macos-<arch>-<ver>.jar
#   Linux   -> .deb + Akane-linux-x64-<ver>.jar
#
# IMPORTANT: the module depends on compose.desktop.currentOs — the installer and
# uber-jar are built only for the OS the build runs on. jpackage cannot
# cross-compile: .dmg is produced only on macOS, .exe only on Windows.
# The jar bundles native skiko for its OS only and won't run on another OS.
#
# Usage:  ruby scripts/build_desktop.rb [--minified]
#   --minified  release task variants with ProGuard.
#   WARNING: akane-desktop/proguard-rules.pro is NOT wired into build.gradle.kts,
#   so default ProGuard rules apply — reflection-based code may break
#   (Koin/Ktor/serialization). Non-minified by default.

require "fileutils"
require_relative "build_common"

minified = ARGV.include?("--minified")

# --- version from build.gradle.kts (packageVersion) ---
gradle_text = File.read(File.join(REPO_ROOT, "akane-desktop/build.gradle.kts"))
version = gradle_text[/packageVersion\s*=\s*"([^"]+)"/, 1] || "unknown"

# --- OS -> installer task and extension ---
os, installer_ext =
  case HOST_OS
  when :windows then [:windows, "exe"]
  when :macos   then [:macos,   "dmg"]
  when :linux   then [:linux,   "deb"]
  else abort "Unknown OS: #{RbConfig::CONFIG['host_os']}"
  end

if minified
  cap = installer_ext[0].upcase + installer_ext[1..]
  installer_task = "packageRelease#{cap}"
  jar_task = "packageReleaseUberJarForCurrentOS"
  bin_dir = "akane-desktop/build/compose/binaries/main-release/#{installer_ext}"
else
  installer_task = "package#{installer_ext.capitalize}"
  jar_task = "packageUberJarForCurrentOS"
  bin_dir = "akane-desktop/build/compose/binaries/main/#{installer_ext}"
end
jar_dir = "akane-desktop/build/compose/jars"

puts "Version: #{version} | OS: #{os} | Minified: #{minified}"
gradle([":akane-desktop:#{installer_task}", ":akane-desktop:#{jar_task}"])

# --- collect artifacts ---
FileUtils.mkdir_p(DIST_DIR)

installer = newest(File.join(REPO_ROOT, bin_dir, "*.#{installer_ext}"))
if installer
  out = File.join(DIST_DIR, "Akane-#{version}-#{os}-installer.#{installer_ext}")
  FileUtils.cp(installer, out)
  puts "INSTALLER -> #{out}"
else
  warn "Installer .#{installer_ext} not found in #{bin_dir}"
end

# uber-jar: name already carries OS+arch, keep it as-is
jar = newest(File.join(REPO_ROOT, jar_dir, "*.jar"))
abort "Uber-jar not found in #{jar_dir}" unless jar
jar_out = File.join(DIST_DIR, File.basename(jar))
FileUtils.cp(jar, jar_out)
puts "JAR -> #{jar_out}  (#{os} only!)"
puts "     run: java -jar #{File.basename(jar)}"

puts "Done."
