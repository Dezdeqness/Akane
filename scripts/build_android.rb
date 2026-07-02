#!/usr/bin/env ruby
# frozen_string_literal: true

# Builds a signed release APK for Android and copies it into dist/.
# Cross-platform: works on Windows and macOS/Linux alike.
#
# Version comes from app.properties, signing config from local.properties.
# local.properties (repo root) must contain:
#   keystore.release=<path to .jks/.keystore>
#   keystore.password=<store password>
#   keystore.key.alias=<key alias>
#   keystore.key.password=<key password>
#
# Usage:  ruby scripts/build_android.rb [--bundle]
#   --bundle  also build the .aab (Android App Bundle) for Google Play

require "fileutils"
require_relative "build_common"

bundle = ARGV.include?("--bundle")

app = read_properties(File.join(REPO_ROOT, "app.properties"))
version = app["app.version"]
version_code = app["app.versionCode"]
puts "Version: #{version} (#{version_code})"

# --- verify signing credentials ---
local_props_path = File.join(REPO_ROOT, "local.properties")
abort "No local.properties — keystore.* keys are required to sign the release." unless File.exist?(local_props_path)

local = read_properties(local_props_path)
required = %w[keystore.release keystore.password keystore.key.alias keystore.key.password]
missing = required.select { |k| local[k].to_s.strip.empty? }
abort "Signing keys missing in local.properties: #{missing.join(', ')}" unless missing.empty?

store = local["keystore.release"]
store = File.join(REPO_ROOT, store) unless Pathname.new(store).absolute?
abort "Keystore not found: #{local['keystore.release']}" unless File.exist?(store)

# --- build ---
tasks = [":akane-android:assembleRelease"]
tasks << ":akane-android:bundleRelease" if bundle
gradle(tasks)

# --- collect artifacts ---
FileUtils.mkdir_p(DIST_DIR)

apk = newest(File.join(REPO_ROOT, "akane-android/build/outputs/apk/release/*.apk"))
abort "APK not found in akane-android/build/outputs/apk/release" unless apk
apk_out = File.join(DIST_DIR, "Akane-#{version}(#{version_code})-release.apk")
FileUtils.cp(apk, apk_out)
puts "APK -> #{apk_out}"

if bundle
  aab = newest(File.join(REPO_ROOT, "akane-android/build/outputs/bundle/release/*.aab"))
  if aab
    aab_out = File.join(DIST_DIR, "Akane-#{version}(#{version_code})-release.aab")
    FileUtils.cp(aab, aab_out)
    puts "AAB -> #{aab_out}"
  end
end

puts "Done."
