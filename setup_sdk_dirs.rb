#!/usr/bin/env ruby

require 'fileutils'

build_types = ["production", "testing", "integration"]
destination = "/var/www/com.buglabs.web.community/shared/sdk/"
source      = "/var/tmp/"
archive     = "/var/tmp/"

build_types.each do |bt|
  # a folder for a buid type
  base_source = source + bt

  if File.exist?(base_source)

    # get dirs which are version-date strings
    puts "Moving stuff in " + base_source
    src_dirs = Dir.entries(base_source).grep(/^\d/)
    dirs = src_dirs.collect {|x| base_source + "/" + x}
    current_dest = destination + bt

    # copy those dirs to their destination in public/sdk/
    puts "Moving it to " + current_dest
    FileUtils.cp_r dirs, current_dest, :verbose => true

    # archive current
    curr_archive = archive + Time.now.strftime("sdk_mv_%Y%m%d%H%M%S")
    Dir.mkdir(curr_archive) unless File.exist?(curr_archive)
    FileUtils.mv(base_source, curr_archive)
    puts "archived " + base_source + " to " + curr_archive

    # if we're production, do the symlinks
    latest_str = src_dirs.sort.reverse.first

    # symlinks for update site
    link = current_dest + "/current"
    #latest_dest = current_dest + "/" + latest_str + "/updatesite"
    latest_dest = current_dest + "/" + latest_str 
    FileUtils.rm(link)
    FileUtils.ln_s(latest_dest, link, {:verbose=>true, :force=>true})

    next if bt != "production"

    # symlinks for full downloads
    ["lin", "mac", "win"].each do |plat|
      link = current_dest + "/dragonfly-" + plat + ".zip"
      latest_dest = current_dest + "/" + latest_str + "/full/dragonfly-" + plat + "-" + latest_str + ".zip"
      FileUtils.rm(link)
      FileUtils.ln_s(latest_dest, link, :verbose=>true)
    end
  end
end

