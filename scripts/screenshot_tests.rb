#!/usr/bin/env ruby
# frozen_string_literal: true

# Roborazzi screenshot tests (Compose Desktop / JVM target).
#
#   ruby scripts/screenshot_tests.rb            # verify all modules (CI default)
#   ruby scripts/screenshot_tests.rb record     # (re)generate baseline PNGs
#   ruby scripts/screenshot_tests.rb verify     # fail on any visual diff
#   ruby scripts/screenshot_tests.rb compare    # write diff images, do not fail
#
# Options:
#   --module=:path   Run for a single Gradle module instead of all of them.
#
# Baselines live in <module>/src/desktopTest/resources/screenshots/ and are
# committed to git. Record after an intentional theme/UI change, review the PNG
# diff, then commit. See docs/screenshot-testing.md.

require_relative "build_common"

ACTIONS = {
  "record"  => "recordRoborazziDesktop",
  "verify"  => "verifyRoborazziDesktop",
  "compare" => "compareRoborazziDesktop",
}.freeze

MODULES = [
  ":common:designsystem",
  ":features:feed",
  ":features:home",
  ":features:genre",
  ":features:profile",
  ":features:personal",
  ":features:franchise",
  ":features:auth",
  ":features:downloads",
  ":features:details",
].freeze

action = "verify"
modules = MODULES

ARGV.each do |arg|
  case arg
  when /\A--module=(.+)\z/
    single = Regexp.last_match(1)
    single = ":#{single}" unless single.start_with?(":")
    modules = [single]
  when "-h", "--help"
    puts File.read(__FILE__).lines[2...18].map { |l| l.sub(/\A# ?/, "") }.join
    exit 0
  when *ACTIONS.keys
    action = arg
  else
    abort "Unknown argument: #{arg}\nUse: record | verify | compare  [--module=:path]"
  end
end

task_suffix = ACTIONS.fetch(action)
tasks = modules.map { |m| "#{m}:#{task_suffix}" }

puts "Screenshot tests: #{action} on #{modules.join(', ')}"
gradle(tasks)

if action == "record"
  total = modules.sum do |m|
    dir = File.join(REPO_ROOT, m.delete_prefix(":").tr(":", "/"), "src/desktopTest/resources/screenshots")
    Dir.glob(File.join(dir, "*.png")).size
  end
  puts "Done. #{total} baseline(s) across #{modules.size} module(s). Review the images, then commit them."
else
  puts "Done (#{action})."
end
