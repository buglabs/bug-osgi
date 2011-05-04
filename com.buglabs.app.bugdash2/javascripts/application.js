/**
 * BUGdash JS functions  
 * @author akweon
 */
if (typeof BL =='undefined') var BL = {}; 
BL.BUGdash = function() { }
/* ----------------------------------- Announcement ----------------------------------- */
BL.BUGdash.Announcement = function() { }
BL.BUGdash.Announcement.prototype = {
	category: '', 
	animate: false, 
	message: '',
	detail: '',
	logged_at: null, 
	init: function(category, message, detail) {
		this.category = category;
		this.message = message; 
		this.detail = detail; 
	}
}
// Manages global announcements
/*
	- be able to display info, warning, error (category) 
	- be able to specify duration (disappear after few sec, stay there, etc) 
	- be able to list all announcements by reverse chronological order (for the session)
	build history item
	<table>
		<tr>
			<td>index</td>
			<td>type (warning, info, error)</td>
			<td>message</td>
			<td>timestamp</td>
		</tr>
	</table>
*/
BL.BUGdash.AnnouncementManager = function() { }
BL.BUGdash.AnnouncementManager.prototype = {
	current: null, 
	history: new Array(),
	tell: function(a, save, callback) {
		if (a.message != '') {
			a.animate = save;
			this.build(a); 
			if (save) 
				this.add_to_history(a);
			if (typeof(callback) == 'function') 
				callback();
			this.current = a; 			 
		}
	},
	add_to_history: function(a) {
		if (this.current == null || (this.current != null && this.current.message != a.message)) {
			a.logged_at = new Date(); 
			this.history.push(a);
			var new_row = jQuery(	'<tr ' + ((this.history.length%2 == 1) ? 'class="alternate"' : '' ) + '><td>' + this.history.length + '</td>' + 
																'<td class="category"><span class="announce ' + a.category + '">&nbsp;</span>' + a.category + '</td>' + 
																'<td>' + a.message + 
																((a.detail == undefined || a.detail == null) ?  '' : '<br /><span class="detail">' + a.detail + '</span>' ) + 
																'</td><td class="nowrap">' + BL.BUGdash.Utilities.formatDate(a.logged_at) + '</td>' + 
														'</tr>');
			new_row.prependTo('table#table_announcement_history > tbody');
		} 
	},
	build: function(announcement) {
			$('#div_announcement').show();
			$('#div_announcement').html("<span class='announce " + announcement.category + "'>&nbsp;</span>" + announcement.message); 
			if (announcement.animate) {
				$('#td_announce').animate({backgroundColor:'#5b5857'},700).animate({backgroundColor:'#A4A5A6'},700);
			}
	},
	toggle_history: function() {
		$('#div_announce_history').toggle(); 
		if ($('#div_announce_history').css('display') == 'none') {
			$('#div_announce_more img').attr('src','/admin.images/statusbar-arrow_down.jpg');
			$('#div_announce_more a').text('More');
		} else {
			$('#div_announce_more img').attr('src','/admin.images/statusbar-arrow_up.jpg');
			$('#div_announce_more a').text('Close');
		}
	}
}
/* ----------------------------------- LogViewer ----------------------------------- */
// manages starting a thread for each log and displays progress 
BL.BUGdash.LogViewer = function() { }
BL.BUGdash.LogViewer.prototype = {
	log_key: '', 
	path: '',
	holder_id: null, 
	holder: null,
	status: 0, 				/* 0-OFF, 1-ON; thread starts/ends */
	refreshRate: 2, 	/* seconds - stop request at 0 */
	refreshObj: null, 
	init: function(log_key, holder_id, path) {
		this.log_key = log_key; 
		this.holder_id = holder_id; 
		this.holder = $('#'+holder_id);
		this.path = path; 
		this.buildUI();
	},
	buildUI: function() {
		var self = this;
		var menu = $('#'+this.holder_id + ' .menu'); 
		var display_button = $('<a href="javascript:void(0);" class="btn-menu">Close [x]</a>').bind('click',function() {self.close()});
		menu.append(display_button);
		display_button = $('<a href="javascript:void(0);" class="btn-menu">Select all</a>').bind('click',function() { self.select_all()});
		menu.append(display_button);
	},
	close: function() {
		this.holder.hide();
		this.unload(); 
	},
	open: function() {
		this.holder.show();
		this.start_viewer(); 
	},
	select_all: function() {
		var div_msg=$('#'+this.holder_id+' .message')[0];
		if (document.selection) { 
			var div = document.body.createTextRange(); // IE
			div.moveToElementText(div_msg);
			div.select();
		}
		else {
			var div = document.createRange(); // FF
			div.setStartBefore(div_msg);
			div.setEndAfter(div_msg);
			window.getSelection().addRange(div);
		} 	
	},
	start_viewer: function() {
		var self = this;	
		$.ajax({
		  url: "/admin_system/start_reading",
		  data: ({key: this.log_key}),
		  cache: false,
		  success: function(text){
				self.refreshObj = setInterval('log_'+self.log_key+'.read_log()',2000);
				self.status = 1; 	
		  }
		});			
	},
	read_log: function() {
		var self = this;	
		$.ajax({
		  url: "/admin_system/read_buffer",
		  data: ({key: this.log_key}),
		  cache: false,
		  success: function(text){
		  	var message_holder = $('#'+self.holder_id + ' .message'); 
		    message_holder.append(self.colorcode(text));
		    message_holder[0].scrollTop = message_holder[0].scrollHeight;
		  }
		});			
	},
	unload: function() {
		// stop all threads; call on page unload 
		if (this.status == 1) {
			$.ajax({
		  	url: "/admin_system/stop_reading",
		  	data: ({key: this.log_key}),
		  	cache: false,
		  	success: function(text){

		  	}
			});			
			clearInterval(this.refreshObj);		
		}
	},
	colorcode: function(input) {
		var out = ''; 
		// colorcode each line based on which keyword is contained
		var lines = input.split("\r\n");
		jQuery.each(lines, function(index, value) {
			if (value != '') {
				var regex = new RegExp(/\[(DEBUG|INFO|ERROR)\]/gi);
				var m = value.match(regex);
				if (m && m.length > 0) {
					out += '<span class="' + m[0].toLowerCase().replace('[','').replace(']','') + '">' + value + '</span><br />';	
				} else {
					out += value + '<br />'; 
				}
			} 
		});
		return out; 	
	}
}

/* ----------------------------------- AppManager ----------------------------------- */

BL.BUGdash.AppManager = function() { }
BL.BUGdash.AppManager.prototype = {
	announcer: null, 
	frm_installed: null, 
	frm_browser: null,
	filter: {search:'', page:1, packages:'', filter_by_packages: false},
	paging: {result_count: 0, page_size: 0, page: 0},
	init: function(installed_id, browser_id) {
		this.frm_installed = $('#'+installed_id)[0]; 
		this.frm_browser = $('#'+browser_id)[0]; 
		this.announcer = parent.announcer; 
	},
	install: function(name) {
		if (confirm('Are you sure you want to install ' + name + '?')) {
			var self = this; 
			this.announcer.tell({category: 'info', message: 'Installing ' + name, animate: true});
			$.ajax({
			  url: "/admin_software/apps_from_bugnet",
			  data: {install: name},
			  type: 'POST',
			  cache: false,
			  success: function(text){
			  	var resp = eval('('+text+')');
					self.refresh_installed();  
					if (resp.status == 'OK') 
						self.announcer.tell({category: 'info', message: 'Finished installing ' + name, animate: true}, true);
					else 
						self.announcer.tell({category: 'error', message: 'There was a problem installing ' + name, detail: resp.message, animate: true}, true);
			  },
			  error: function(text) {
					self.announcer.tell({category: 'error', message: 'There was a problem installing ' + name, animate: true}, true);		  
			  }
			});								
							
		}
	},
	uninstall: function(name, bundleId) {
		if (confirm('Are you sure you want to uninstall ' + name + '?')) {
			var self = this; 
			this.announcer.tell({category: 'info', message: 'Uninstalling ' + name, animate: true});		
			$.ajax({
			  url: "/admin_software/apps_installed",
			  data: {task: 'uninstall', bundleId: bundleId},
			  type: 'POST',
			  cache: false,
			  success: function(text){
			  	var resp = eval('('+text+')');
					self.refresh_installed();  
					if (resp.status == 'OK') 
						self.announcer.tell({category: 'info', message: 'Finished uninstalling ' + name, animate: true}, true);
					else 
						self.announcer.tell({category: 'error', message: 'There was a problem uninstalling ' + name, detail: resp.message, animate: true}, true);
			  },
			  error: function(text) {
					self.announcer.tell({category: 'error', message: 'There was a problem uninstalling ' + name, animate: true}, true);		  
			  }
			});								
		}
	},
	start: function(name, bundleId) {
		if (confirm('Are you sure you want to start ' + name + '?')) {
			var self = this; 
			this.announcer.tell({category: 'info', message: 'Starting ' + name, animate: true});		
			$.ajax({
			  url: "/admin_software/apps_installed",
			  data: {task: 'start', bundleId: bundleId},
			  type: 'POST',
			  cache: false,
			  success: function(text){
			  	var resp = eval('('+text+')');
					self.refresh_installed();  
					if (resp.status == 'OK') 
						self.announcer.tell({category: 'info', message: 'App ' + name + ' is started', animate: true}, true);
					else 
						self.announcer.tell({category: 'error', message: 'There was a problem starting ' + name, detail: resp.message, animate: true}, true);
			  },
			  error: function(text) {
					self.announcer.tell({category: 'error', message: 'There was a problem starting ' + name, animate: true}, true);		  
			  }
			});								
		}	
	},
	stop: function(name, bundleId) {
		if (confirm('Are you sure you want to stop ' + name + '?')) {
			var self = this; 
			this.announcer.tell({category: 'info', message: 'Stopping ' + name, animate: true});		
			$.ajax({
			  url: "/admin_software/apps_installed",
			  data: {task: 'stop', bundleId: bundleId},
			  type: 'POST',
			  cache: false,
			  success: function(text){
			  	var resp = eval('('+text+')');
					self.refresh_installed();  
					if (resp.status == 'OK') 
						self.announcer.tell({category: 'info', message: 'App ' + name + ' is stopped', animate: true}, true);
					else 
						self.announcer.tell({category: 'error', message: 'There was a problem stopping ' + name, detail: resp.message, animate: true}, true);
			  },
			  error: function(text) {
					self.announcer.tell({category: 'error', message: 'There was a problem stopping ' + name, animate: true}, true);		  
			  }
			});								
		}		
	},
	refresh_installed: function() {
		//this.frm_installed.src = this.frm_installed.src; // prevent caching
		var time = new Date(); 
		this.frm_installed.src = '/admin_software/apps_installed?time='+time.getMilliseconds(); 
	},
	refresh_browser: function() {
		this.frm_browser.src = this.frm_browser.src; 
	},
	lookup: function(search_term) {
		this.filter.search = search_term; 
		this.filter.page = 1; 
		this.load_browser(); 
	},
	nextPage: function() {
		this.filter.page++;
		this.load_browser(); 
	},
	prevPage: function() {
		if (this.filter.page > 1)
			this.filter.page--;
		this.load_browser(); 
	},
	filterPackages: function(doFilter) {
		//console.debug("doFilter: " + doFilter);
		this.filter.filter_by_packages = doFilter; 
		this.load_browser(); 
	},
	load_browser: function() {
		var params = 'search='+this.filter.search+'&page='+this.filter.page + '&filter_by_packages=' + this.filter.filter_by_packages;
		if (this.filter.packages.length > 0) 
			params += '&packages=';
		//console.debug(params);
		this.frm_browser.src = '/admin_software/apps_from_bugnet?' + params; 
	},
	login: function(user, pwd) {
		$.ajax({
			url: '/admin_bugnet/bugnet', 
			type: 'POST',
			data: 'inp_username='+user+'&inp_password='+pwd+'&format=json',
			dataType: 'json',
			success: function(data) {
				if (parent.announcer) 
					parent.announcer.tell(data, true); 
				if (data.category != 'error') 
					parent.refresh_main_iframe('/admin_software/apps'); 
			}, 
			error: function() {
			
			}
		});
	},
	logout: function() {
		$.ajax({
			url: '/admin_bugnet/bugnet', 
			type: 'POST',
			data: 'btn_submit=Logout&format=json',
			dataType: 'json',
			success: function(data) {
				if (parent.announcer) 
					parent.announcer.tell(data, true); 
				parent.refresh_main_iframe('/admin_software/apps'); 
			}, 
			error: function() {
			
			}
		});		
	}
}	
/* ------------------------------- PackageUpgrader ---------------------------- */
// manages starting a thread for package upgrade and displays progress  
BL.BUGdash.PackageUpgrader = function() { }
BL.BUGdash.PackageUpgrader.prototype = {
	refreshObj: null, 
	waitForRebootObj: null, 
	init: function() {
	},
	update_progress: function() {
		var self = this;	
		$.ajax({
		  url: "read_update_package",
		  cache: false,
		  success: function(text){
		  		var cmd = eval('('+text+')');
		  		// expects {command: '', description: '', output: '', percent: ''}
		  		if (cmd.output.length > 0) {
			  		var message_holder 	= $('#div_upgrade_progress'); 
			  		var status_holder 	= $('#div_status_message h3'); 
			    	message_holder.append(self.colorcode(cmd.output));
			    	message_holder[0].scrollTop = message_holder[0].scrollHeight;
			    	status_holder.text(cmd.percent + '% -- ' + cmd.description); 
			    	if (cmd.output.indexOf('Nothing to be done') > -1 && cmd.output.indexOf('upgrade done') == -1) {
			    		self.stop(); 
			    		status_holder.text('100% -- Up to date'); 
			    	} else if (cmd.percent == '100') {
			    		self.stop(); 
			    		status_holder.text('100% -- Completed'); 
			    		setTimeout('upgrader.wait_to_reboot()', 3000);			    	
			    	}
		    	}
		  },
		  error: function(xhr, textStatus, errorThrown) { self.stop(); }
		});		
	},
	start: function() {
		var self = this;	
		var status_holder 	= $('#div_status_message h3'); 
		status_holder.text('Starting...');
		$.ajax({
		  url: "update_packages",
		  cache: false,
		  success: function(text){
				self.refreshObj = setInterval('upgrader.update_progress()',2000);	
		  },
		  error: function() {
		  	status_holder.text('There was a problem');
		  }
		});				
	},
	stop: function() {
		clearInterval(this.refreshObj);
		$.ajax({
		  url: "read_update_package?task=stop",
		  cache: false,
		  success: function(text){ }
		});					
	},
	wait_to_reboot: function() {
		// until BUG finishes rebooting, display an appropriate message 
		$('#div_status_message h3').text('Upgrade is completed.');
		if (confirm('Do you want to reboot now?')) {
			$.ajax({
				url: "/admin_util/task",
				data: {task: 'delete_storage_and_restart'},
				type: 'POST',
				success: function(text) {
					this.waitForRebootObj = setInterval('upgrader.check_status()',10000);	
				}
			});
		}
	},
	check_status: function() {
		var self = this; 
			$.ajax({
				url: "/admin_util/dashboard_status", 
				cache: false, 
				success: function() {
					clearInterval(self.waitForRebootObj); 
					$('#div_status_message h3').text('Your BUG has been upgraded to the latest version');
				},
				error: function() {
					// keep going until certain duration 
				}
			}); 	
	},
	colorcode: function(input) {
			/*
				- URL
				- [ERROR]
				- Nothing to be done 
			*/
		var out = ''; 
		// colorcode each line based on which keyword is contained
		var lines = input.split("<br />");
		jQuery.each(lines, function(index, value) {
			if (value != '') {
				var regex_error = new RegExp(/\[(DEBUG|INFO|ERROR)\]/gi);
				var regex_url = new RegExp(/http:\/\/[\w|.|\/|\-|\+]*/gi);
				var m_error = value.match(regex_error);
				var m_url = value.match(regex_url); 
				if (m_error && m_error.length > 0) {
					out += '<span class="' + m_error[0].toLowerCase().replace('[','').replace(']','') + '">' + value + '</span><br />';	
				} else if (m_url && m_url.length > 0) { 
					out += value.replace(m_url[0], '<span class="url">' + m_url[0]+ '</span><br />'); 
				} else {
					out += value + '<br />'; 
				}
			} 
		});
		return out; 	
	},
	toggle_detail: function() {
		$('#div_upgrade_progress_holder').toggle();
	}
}
/* ------------------------------- ConfigurationManager ---------------------------- */
BL.BUGdash.ConfigurationManager = function() { }
BL.BUGdash.ConfigurationManager.prototype = {
	announcer: null, 
	init: function() {
		this.announcer = parent.announcer; 
	},
	display_properties: function(pid) {
		var self = this;	
		$.ajax({
		  url: "/admin_system/manage_configuration_property",
		  data: ({pid: pid}),
		  cache: false,
		  success: function(text){
		  	var props = eval('('+text+')');
		  	self.write_properties(pid, props); 
		  	self.show_properties(pid);
		  }
		});			
	},
	write_properties: function(pid, props) {
		var properties_row = 'config_' + BL.BUGdash.ConfigurationManager.transform_to_id(pid) + '_properties';
		var content = '<form id="frm_edit_' + properties_row + '"><table class="config_detail">'; 
		content += '<input type="hidden" name="task" value="update" />';
		content += '<input type="hidden" name="pid" value="' + pid + '" />';
		content += '<tr class="header""><td class="prop">Property key</td><td>Value</td><td>Delete</td></tr>';
		var prop_row;
		$.each(props, function(index, prop) {
			prop_row = properties_row + '_' + BL.BUGdash.ConfigurationManager.transform_to_id(prop.key); 
			content += '<tr id="' + prop_row + '">';
			content += '<td class="prop"><span class="read">' + prop.key + '</span><input type="hidden" name="prop_key" value="' + prop.key + '" /></td>';
			content += '<td><span class="edit"><input type="text" name="prop_value" value="' + prop.value + '" class="input_prop_value" /></span></td>';
			content += '<td><input type="checkbox" name="chb_delete" value="' + prop.key + '" /></td>';
			content += '</tr>';
		});
		content += '<tr>';
		content += '<td><span class="label">New property</span> &nbsp;<input type="text" name="txt_new_property_key" value="" /></td>';
		content += '<td><input type="text" name="txt_new_property_value" value="" class="input_prop_value" /></td><td></td>';
		content += '</tr>';
		content += '<tr>';
		content += '<td></td><td><input type="button" value="Submit" onclick="config_manager.save_properties(\'' + pid + '\');" class="submit-small" /> &nbsp;<a href="javascript:void(0);" onclick="config_manager.hide_properties(\'' + pid + '\');" class="secondary">Cancel</a></td>';
		content += '</tr>';
		content += '</table></form>';
		var properties_html = jQuery(content);
		$('#'+properties_row+ ' > td').html('');
		properties_html.appendTo('tr#'+properties_row+' > td');
	},
	save_properties: function(pid) {
		var properties_row = 'config_' + BL.BUGdash.ConfigurationManager.transform_to_id(pid) + '_properties';
		var self = this;	
		$.ajax({
		  url: "/admin_system/manage_configuration_property",
		  data: $('#frm_edit_'+properties_row).serialize(),
		  type: 'POST',
		  cache: false,
		  success: function(text){
				self.display_properties(pid);
				self.announcer.tell({category: 'info', message: 'Saved configuration ' + pid, animate: true}, true);
		  },
		  error: function(text) {
				self.announcer.tell({category: 'error', message: 'There was a problem saving configuration ' + pid, animate: true}, true);		  
		  }
		});		
	},
	show_properties: function(pid) {
		var config_row = 'config_' + BL.BUGdash.ConfigurationManager.transform_to_id(pid); 	
  	$('tr#'+config_row+' a.show').hide(); 
  	$('tr#'+config_row+' a.hide').show();	
	},
	hide_properties: function(pid) {
		var config_row = 'config_' + BL.BUGdash.ConfigurationManager.transform_to_id(pid); 	
  	$('tr#'+config_row+' a.show').show(); 
  	$('tr#'+config_row+' a.hide').hide();		
		$('#'+config_row+'_properties > td').html('');  	
	},
	delete_config: function(pid) {
		if (confirm('Are you sure you want to delete ' + pid + '?')) {
			$("#txt_remove_pid").attr("value",pid);
			this.announcer.tell({category: 'info', message: 'Deleted configuration ' + pid, animate: true}, true);
			$("#frm_delete").submit();
		}
	}
}
BL.BUGdash.ConfigurationManager.transform_to_id = function(str) {
	return str.replace(/ /gi, '_').replace(/\./gi, '_');
}
/* ----------------------------------- Rebooter ----------------------------------- */
BL.BUGdash.Rebooter = function() { }
BL.BUGdash.Rebooter.prototype = {
	waitForRebootObj: null, 
	reboot: function() {
		if (confirm('Are you sure you want to reboot your BUG?')) {
			$('#div_status_message h3').text('We\'re about to restart.');
			var self = this; 
			$.ajax({
				url: "/admin_util/task",
				data: {task: 'reboot'},
				type: 'POST',
				success: function(text) {
					self.waitForRebootObj = setInterval('rebooter.check_status()',10000);	
					$('#div_status_message h3').text('Rebooting...');					
				}
			});
		}	
	},
	check_status: function() {
		var self = this; 
		$.ajax({
			url: "/admin_util/dashboard_status", 
			cache: false, 
			success: function() {
				clearInterval(self.waitForRebootObj); 
				$('#div_status_message h3').text('We\'re back');
			},
			error: function() { }
		}); 		
	}
}
/* ----------------------------------- UpdateChecker  ------------------------------ */
BL.BUGdash.PackageUpdateChecker = function() { }
BL.BUGdash.PackageUpdateChecker.prototype = {
	refreshObj: null, 		
	announcer: null, 
	init: function() {
		this.announcer = parent.announcer; 
	},	
	start: function() {
		var self = this;	
		var status_holder = $('#span_status'); 
		status_holder.html('Checking for new updates... &nbsp; <a href="javascript:void(0);" onclick="update_checker.stop(\'Stopped checking for updates.\');">Cancel</a> <br /><span class="quiet">This may take few minutes</span>');
		$('#div_new_updates').text('');
		$.ajax({
		  url: "start_checking_new_updates",
		  cache: false,
		  success: function(text){
		  	var msg = eval('('+text+')');
				self.announcer.tell(msg, true); 
				if (msg.category == 'info') {
					self.refreshObj = setInterval('update_checker.update_progress()',2000);	
				} else if (msg.category == 'error') {
					$('#span_status').html('No network connection.&nbsp; <a href="javascript:void(0);" onclick="update_checker.start();">Try again</a>');
				}
		  },
		  error: function() { status_holder.text('There was a problem'); }
		});			
	},
	update_progress: function() {
		var self = this;	
		$.ajax({
		  url: "read_new_updates",
		  cache: false,
		  success: function(text){
		  		if (text.length > 0) {
			  		var message_holder 	= $('#div_new_updates'); 
			  		var status_holder 	= $('#span_status'); 
			  		var lines; 
			  		if (text.indexOf('Upgrading') > -1) { // show only what matters
			  			lines = text.split('<br />');
			  			for(var i=0; i<lines.length; i++) {
			  				if (lines[i].indexOf('Upgrading') > -1) {
			  					message_holder.append(lines[i].replace('Upgrading','').replace('...','') + '<br />');	
			  				}
			  			}
			    	}
			    	if (text.indexOf('Nothing to be done') > -1) {
			    		self.stop('Finished'); 				
			    	}
		    	}
		  },
		  error: function() { self.stop(); }
		});			
	},
	stop: function(msg) {
		clearInterval(this.refreshObj);
		if (!msg)
			msg = 'Checking for new updates...';
		this.announcer.tell({category:'info',message:'Finished checking for updates'}, true); 			
		$.ajax({
		  url: "read_new_updates?task=stop",
		  cache: false,
		  success: function(text){ 
				$('#span_status').html(msg + '&nbsp; <a href="javascript:void(0);" onclick="update_checker.start();">Start</a>');			  	
		  }
		});				
	}		
}	
/* ----------------------------------- Utilities ----------------------------------- */
BL.BUGdash.Utilities = function() { }
BL.BUGdash.Utilities.checkConnectivity = function(callback_success, callback_error) {
	$.ajax({
		url: "/admin_util/internet_status",
		cache: false, 
		success: function(text) {
			if (typeof(callback_success) == 'function') { callback_success(text); } 
		},
		error: function() {
			if (typeof(callback_error) == 'function') { callback_error(text); }  
		}
	}); 
}
BL.BUGdash.Utilities.displayProcesses = function(callback_success, callback_error) {
	$.ajax({
		url: "/admin_util/task?task=display_process",
		cache: false, 
		success: function(text) {
			if (typeof(callback_success) == 'function') { callback_success(text); } 
		},
		error: function() {
			if (typeof(callback_error) == 'function') { callback_error(text); }  
		}
	}); 
}
BL.BUGdash.Utilities.formatDate = function(date) {
	var today = new Date(); 
	if (date.getFullYear() == today.getFullYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate()) {
		return date.format("h:MM TT");
	} else {
		return date.format("mmmm d, yyyy h:MM TT");
	}
}
BL.BUGdash.Utilities.checkEnter = function(e, callback) {
	var characterCode;
	if(e && e.which){
		e = e;
		characterCode = e.which;
	}
	else{
		e = event;
		characterCode = e.keyCode;
	}	 
	if(characterCode == 13){
		 if (typeof callback == 'function') callback();
		return false;
	}
	return true;
}

BL.BUGdash.Utilities.refresh = function() {
	parent.location = '/admin';
}
	
/* ----------------------------------- Libraries ----------------------------------- */
/*
 * Date Format 1.2.3 (c) 2007-2009 Steven Levithan <stevenlevithan.com> MIT license
 */
var dateFormat=function(){var token=/d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,timezone=/\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,timezoneClip=/[^-+\dA-Z]/g,pad=function(val,len){val=String(val);len=len||2;while(val.length<len)val="0"+val;return val;};return function(date,mask,utc){var dF=dateFormat;if(arguments.length==1&&Object.prototype.toString.call(date)=="[object String]"&&!/\d/.test(date)){mask=date;date=undefined;}
date=date?new Date(date):new Date;if(isNaN(date))throw SyntaxError("invalid date");mask=String(dF.masks[mask]||mask||dF.masks["default"]);if(mask.slice(0,4)=="UTC:"){mask=mask.slice(4);utc=true;}
var _=utc?"getUTC":"get",d=date[_+"Date"](),D=date[_+"Day"](),m=date[_+"Month"](),y=date[_+"FullYear"](),H=date[_+"Hours"](),M=date[_+"Minutes"](),s=date[_+"Seconds"](),L=date[_+"Milliseconds"](),o=utc?0:date.getTimezoneOffset(),flags={d:d,dd:pad(d),ddd:dF.i18n.dayNames[D],dddd:dF.i18n.dayNames[D+7],m:m+1,mm:pad(m+1),mmm:dF.i18n.monthNames[m],mmmm:dF.i18n.monthNames[m+12],yy:String(y).slice(2),yyyy:y,h:H%12||12,hh:pad(H%12||12),H:H,HH:pad(H),M:M,MM:pad(M),s:s,ss:pad(s),l:pad(L,3),L:pad(L>99?Math.round(L/10):L),t:H<12?"a":"p",tt:H<12?"am":"pm",T:H<12?"A":"P",TT:H<12?"AM":"PM",Z:utc?"UTC":(String(date).match(timezone)||[""]).pop().replace(timezoneClip,""),o:(o>0?"-":"+")+pad(Math.floor(Math.abs(o)/60)*100+Math.abs(o)%60,4),S:["th","st","nd","rd"][d%10>3?0:(d%100-d%10!=10)*d%10]};return mask.replace(token,function($0){return $0 in flags?flags[$0]:$0.slice(1,$0.length-1);});};}();dateFormat.masks={"default":"ddd mmm dd yyyy HH:MM:ss",shortDate:"m/d/yy",mediumDate:"mmm d, yyyy",longDate:"mmmm d, yyyy",fullDate:"dddd, mmmm d, yyyy",shortTime:"h:MM TT",mediumTime:"h:MM:ss TT",longTime:"h:MM:ss TT Z",isoDate:"yyyy-mm-dd",isoTime:"HH:MM:ss",isoDateTime:"yyyy-mm-dd'T'HH:MM:ss",isoUtcDateTime:"UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"};dateFormat.i18n={dayNames:["Sun","Mon","Tue","Wed","Thu","Fri","Sat","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],monthNames:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","January","February","March","April","May","June","July","August","September","October","November","December"]};Date.prototype.format=function(mask,utc){return dateFormat(this,mask,utc);};
/*
 * jQuery corner plugin: simple corner rounding http://jquery.malsup.com/corner/ version 1.99 (28-JUL-2009)
 * Dual licensed under the MIT and GPL licenses
 */
;(function($){var expr=(function(){if(!$.browser.msie)return false;var div=document.createElement('div');try{div.style.setExpression('width','0+0');}
catch(e){return false;}
return true;})();function sz(el,p){return parseInt($.css(el,p))||0;};function hex2(s){var s=parseInt(s).toString(16);return(s.length<2)?'0'+s:s;};function gpc(node){for(;node&&node.nodeName.toLowerCase()!='html';node=node.parentNode){var v=$.css(node,'backgroundColor');if(v=='rgba(0, 0, 0, 0)')
continue;if(v.indexOf('rgb')>=0){var rgb=v.match(/\d+/g);return'#'+hex2(rgb[0])+hex2(rgb[1])+hex2(rgb[2]);}
if(v&&v!='transparent')
return v;}
return'#ffffff';};function getWidth(fx,i,width){switch(fx){case'round':return Math.round(width*(1-Math.cos(Math.asin(i/width))));case'cool':return Math.round(width*(1+Math.cos(Math.asin(i/width))));case'sharp':return Math.round(width*(1-Math.cos(Math.acos(i/width))));case'bite':return Math.round(width*(Math.cos(Math.asin((width-i-1)/width))));case'slide':return Math.round(width*(Math.atan2(i,width/i)));case'jut':return Math.round(width*(Math.atan2(width,(width-i-1))));case'curl':return Math.round(width*(Math.atan(i)));case'tear':return Math.round(width*(Math.cos(i)));case'wicked':return Math.round(width*(Math.tan(i)));case'long':return Math.round(width*(Math.sqrt(i)));case'sculpt':return Math.round(width*(Math.log((width-i-1),width)));case'dog':return(i&1)?(i+1):width;case'dog2':return(i&2)?(i+1):width;case'dog3':return(i&3)?(i+1):width;case'fray':return(i%2)*width;case'notch':return width;case'bevel':return i+1;}};$.fn.corner=function(o){if(this.length==0){if(!$.isReady&&this.selector){var s=this.selector,c=this.context;$(function(){$(s,c).corner(o);});}
return this;}
o=(o||"").toLowerCase();var keep=/keep/.test(o);var cc=((o.match(/cc:(#[0-9a-f]+)/)||[])[1]);var sc=((o.match(/sc:(#[0-9a-f]+)/)||[])[1]);var width=parseInt((o.match(/(\d+)px/)||[])[1])||10;var re=/round|bevel|notch|bite|cool|sharp|slide|jut|curl|tear|fray|wicked|sculpt|long|dog3|dog2|dog/;var fx=((o.match(re)||['round'])[0]);var edges={T:0,B:1};var opts={TL:/top|tl/.test(o),TR:/top|tr/.test(o),BL:/bottom|bl/.test(o),BR:/bottom|br/.test(o)};if(!opts.TL&&!opts.TR&&!opts.BL&&!opts.BR)
opts={TL:1,TR:1,BL:1,BR:1};var strip=document.createElement('div');strip.style.overflow='hidden';strip.style.height='1px';strip.style.backgroundColor=sc||'transparent';strip.style.borderStyle='solid';return this.each(function(index){var pad={T:parseInt($.css(this,'paddingTop'))||0,R:parseInt($.css(this,'paddingRight'))||0,B:parseInt($.css(this,'paddingBottom'))||0,L:parseInt($.css(this,'paddingLeft'))||0};if(typeof this.style.zoom!=undefined)this.style.zoom=1;if(!keep)this.style.border='none';strip.style.borderColor=cc||gpc(this.parentNode);var cssHeight=$.curCSS(this,'height');for(var j in edges){var bot=edges[j];if((bot&&(opts.BL||opts.BR))||(!bot&&(opts.TL||opts.TR))){strip.style.borderStyle='none '+(opts[j+'R']?'solid':'none')+' none '+(opts[j+'L']?'solid':'none');var d=document.createElement('div');$(d).addClass('jquery-corner');var ds=d.style;bot?this.appendChild(d):this.insertBefore(d,this.firstChild);if(bot&&cssHeight!='auto'){if($.css(this,'position')=='static')
this.style.position='relative';ds.position='absolute';ds.bottom=ds.left=ds.padding=ds.margin='0';if(expr)
ds.setExpression('width','this.parentNode.offsetWidth');else
ds.width='100%';}
else if(!bot&&$.browser.msie){if($.css(this,'position')=='static')
this.style.position='relative';ds.position='absolute';ds.top=ds.left=ds.right=ds.padding=ds.margin='0';if(expr){var bw=sz(this,'borderLeftWidth')+sz(this,'borderRightWidth');ds.setExpression('width','this.parentNode.offsetWidth - '+bw+'+ "px"');}
else
ds.width='100%';}
else{ds.position='relative';ds.margin=!bot?'-'+pad.T+'px -'+pad.R+'px '+(pad.T-width)+'px -'+pad.L+'px':(pad.B-width)+'px -'+pad.R+'px -'+pad.B+'px -'+pad.L+'px';}
for(var i=0;i<width;i++){var w=Math.max(0,getWidth(fx,i,width));var e=strip.cloneNode(false);e.style.borderWidth='0 '+(opts[j+'R']?w:0)+'px 0 '+(opts[j+'L']?w:0)+'px';bot?d.appendChild(e):d.insertBefore(e,d.firstChild);}}}});};$.fn.uncorner=function(){$('div.jquery-corner',this).remove();return this;};})(jQuery);
/*
 * jQuery Color Animations; copyright 2007 John Resig; released under the MIT and GPL licenses.
 */
(function(jQuery){jQuery.each(['backgroundColor','borderBottomColor','borderLeftColor','borderRightColor','borderTopColor','color','outlineColor'],function(i,attr){jQuery.fx.step[attr]=function(fx){if(fx.state==0){fx.start=getColor(fx.elem,attr);fx.end=getRGB(fx.end);}
fx.elem.style[attr]="rgb("+[Math.max(Math.min(parseInt((fx.pos*(fx.end[0]-fx.start[0]))+fx.start[0]),255),0),Math.max(Math.min(parseInt((fx.pos*(fx.end[1]-fx.start[1]))+fx.start[1]),255),0),Math.max(Math.min(parseInt((fx.pos*(fx.end[2]-fx.start[2]))+fx.start[2]),255),0)].join(",")+")";}});function getRGB(color){var result;if(color&&color.constructor==Array&&color.length==3)
return color;if(result=/rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(color))
return[parseInt(result[1]),parseInt(result[2]),parseInt(result[3])];if(result=/rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(color))
return[parseFloat(result[1])*2.55,parseFloat(result[2])*2.55,parseFloat(result[3])*2.55];if(result=/#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(color))
return[parseInt(result[1],16),parseInt(result[2],16),parseInt(result[3],16)];if(result=/#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(color))
return[parseInt(result[1]+result[1],16),parseInt(result[2]+result[2],16),parseInt(result[3]+result[3],16)];return colors[jQuery.trim(color).toLowerCase()];}
function getColor(elem,attr){var color;do{color=jQuery.curCSS(elem,attr);if(color!=''&&color!='transparent'||jQuery.nodeName(elem,"body"))
break;attr="backgroundColor";}while(elem=elem.parentNode);return getRGB(color);};var colors={aqua:[0,255,255],azure:[240,255,255],beige:[245,245,220],black:[0,0,0],blue:[0,0,255],brown:[165,42,42],cyan:[0,255,255],darkblue:[0,0,139],darkcyan:[0,139,139],darkgrey:[169,169,169],darkgreen:[0,100,0],darkkhaki:[189,183,107],darkmagenta:[139,0,139],darkolivegreen:[85,107,47],darkorange:[255,140,0],darkorchid:[153,50,204],darkred:[139,0,0],darksalmon:[233,150,122],darkviolet:[148,0,211],fuchsia:[255,0,255],gold:[255,215,0],green:[0,128,0],indigo:[75,0,130],khaki:[240,230,140],lightblue:[173,216,230],lightcyan:[224,255,255],lightgreen:[144,238,144],lightgrey:[211,211,211],lightpink:[255,182,193],lightyellow:[255,255,224],lime:[0,255,0],magenta:[255,0,255],maroon:[128,0,0],navy:[0,0,128],olive:[128,128,0],orange:[255,165,0],pink:[255,192,203],purple:[128,0,128],violet:[128,0,128],red:[255,0,0],silver:[192,192,192],white:[255,255,255],yellow:[255,255,0]};})(jQuery);