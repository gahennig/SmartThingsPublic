/**
 *  Thinking Cleaner
 *
 *	Smartthings Devicetype
 *  Variation of the Sidney Johnson "sidjohn1" Device Handler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Version: 1.0 - Initial Version
 *	Version: 1.1 - Fixed installed and updated functions
 *	Version: 1.2 - Added error tracking, and better icons, link state
 *	Version: 1.3 - Better error tracking, error correction and the ability to change the default port (thx to sidhartha100), fix a bug that prevented auto population of deviceNetworkId
 *	Version: 1.4 - Added bin status and code clean up
 *	Version: 1.4.1 - Added poll on inilizition, better error handling, inproved clean button
 *	Version: 1.4.2 - Added JSON response mapping, Multi attribute status tile (more statuses), all new icons, modified labels and buttons.
 *
 */
import groovy.json.JsonSlurper

metadata {
	definition (name: "Thinking Cleaner", namespace: "pmjoen", author: "Patrick Mjoen") {
		capability "Battery"
		capability "Polling"
		capability "Refresh"
		capability "Switch"
		capability "Tone"
        
		command "refresh"
		command "spot"

		attribute "network","string"
		attribute "bin","string"
	}
    
	preferences {
		input("ip", "text", title: "IP Address", description: "Your Thinking Cleaner Address", required: true, displayDuringSetup: true)
		input("port", "number", title: "Port Number", description: "Your Thinking Cleaner Port Number (Default:80)", defaultValue: "80", required: true, displayDuringSetup: true)
	}

	tiles {
		multiAttributeTile(name:"status", type: "lighting", width: 3, height: 2) {
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
				attributeState "default", label:'unknown', icon: "st.unknown.unknown.unknown"
				attributeState "charging", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18129579/eef29f04-6f50-11e6-909d-2dfda7345aab.png", backgroundColor: "#FFEA00"
				attributeState "cleaning", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#79b821"
				attributeState "docked", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#D3D3D3"
				attributeState "docking", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#E5E500"
				attributeState "error", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18101836/e18bb9cc-6eb5-11e6-9a6c-830215f6715f.png", backgroundColor: "#FF0000"
				attributeState "paused", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18101564/d28f0a9c-6eb4-11e6-989e-3401781cb9aa.png", backgroundColor: "#D3D3D3"
				attributeState "delayed", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18101578/e6d34bda-6eb4-11e6-8600-3ccc0165d9c8.png", backgroundColor: "#D3D3D3"
				attributeState "findme", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18101597/f5e07a8a-6eb4-11e6-8475-784a990d0f71.png", backgroundColor: "#FFEA00"
				attributeState "waiting", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#D3D3D3"
			 }
             tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
				attributeState "batery", label:'${currentValue}% Battery'
			}
         }
		standardTile("beep", "device.beep", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state "beep", label:'beep', action:"tone.beep", icon:"st.quirky.spotter.quirky-spotter-sound-on", backgroundColor:"#ffffff"
		}
		standardTile("bin", "device.bin", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			state ("empty", label:'Bin Empty', icon: "st.Kids.kids10", backgroundColor: "#79b821")
			state ("full", label:'Bin Full', icon: "st.Kids.kids19", backgroundColor: "#bc2323")
		}
		standardTile("clean", "device.switch", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
//			state("on", label: 'dock', action: "switch.off", icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#79b821", nextState:"off")
			state("off", label: 'clean', action: "switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#ffffff", nextState:"on")
		}
		standardTile("network", "device.network", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			state ("Connected", label:'Link Good', icon: "https://cloud.githubusercontent.com/assets/8125308/18098635/b2b266da-6ea8-11e6-8ec0-6f947d7d7a67.png", backgroundColor: "#79b821")
			state ("Not Connected", label:'Link Bad', icon: "https://cloud.githubusercontent.com/assets/8125308/18098653/c4d8aeb4-6ea8-11e6-855f-e5fe50f81a29.png", backgroundColor: "#ff0000")
		}
		standardTile("spot", "device.spot", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state("spot", label: 'spot', action: "spot", icon: "https://cloud.githubusercontent.com/assets/8125308/18099787/d34ecbfe-6ead-11e6-89de-7ed6ffcccfba.png", backgroundColor: "#ffffff")
		}
		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("default", action:"refresh.refresh", icon:"st.secondary.refresh")
		}
        standardTile("cleanbutton", "device.switch", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state("on", label: 'cleaning', action: "switch.off", icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#79b821", nextState:"off")
			state("off", label: 'docked', action: "switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18097626/366bb54e-6ea4-11e6-9a86-a63dfd630719.png", backgroundColor: "#ffffff", nextState:"on")
		}

		main("cleanbutton")
			details(["status","spot","clean","beep","network","bin","battery","refresh"])
		}
}

def parse(String description) {
	def map
	def headerString
	def bodyString
	def slurper
	def result

	map = stringToMap(description)
	headerString = new String(map.headers.decodeBase64())
	if (headerString.contains("200 OK")) {
		bodyString = new String(map.body.decodeBase64())
		slurper = new JsonSlurper()
		result = slurper.parseText(bodyString)
		switch (result.action) {
			case "command":
				sendEvent(name: 'network', value: "Connected" as String)
			break;
			case "full_status":
				sendEvent(name: 'network', value: "Connected" as String)
				sendEvent(name: 'battery', value: result.power_status.battery_charge as Integer)
			switch (result.tc_status.bin_status) {
				case "0":
					sendEvent(name: 'bin', value: "empty" as String)
				break;
				case "1":
					sendEvent(name: 'bin', value: "full" as String)
				break;
			}
			switch (result.power_status.cleaner_state) {
				case "st_base":
					sendEvent(name: 'status', value: "docked" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_recon":
					sendEvent(name: 'status', value: "charging" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_full":
					sendEvent(name: 'status', value: "charging" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_trickle":
					sendEvent(name: 'status', value: "charging" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_wait":
					sendEvent(name: 'status', value: "waiting" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug":
					sendEvent(name: 'status', value: "docking" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_recon":
					sendEvent(name: 'status', value: "charging" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_full":
					sendEvent(name: 'status', value: "charging" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_trickle":
					sendEvent(name: 'status', value: "charging" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_wait":
					sendEvent(name: 'status', value: "waiting" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_stopped":
					sendEvent(name: 'status', value: "paused" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_cleanstop":
					sendEvent(name: 'status', value: "paused" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_delayed":
					sendEvent(name: 'status', value: "delayed" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_pickup":
					sendEvent(name: 'status', value: "paused" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_locate":
					sendEvent(name: 'status', value: "findme" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_clean":
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "cleaning" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
						sendEvent(name: 'status', value: "error" as String)
						sendEvent(name: 'switch', value: "on" as String)
						log.debug result.power_status.cleaner_state
					}
				break;
				case "st_clean_spot":
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "cleaning" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
						sendEvent(name: 'status', value: "error" as String)
						sendEvent(name: 'switch', value: "on" as String)
						log.debug result.power_status.cleaner_state
					}
				break;
				case "st_clean_max":
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "cleaning" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
						sendEvent(name: 'status', value: "error" as String)
						sendEvent(name: 'switch', value: "on" as String)
						log.debug result.power_status.cleaner_state
					}
				break;
				case "st_dock":
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "docking" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
						sendEvent(name: 'status', value: "error" as String)
						log.debug result.power_status.cleaner_state
					}
				break;
				case "st_off":
					sendEvent(name: 'switch', value: "off" as String)
					sendEvent(name: 'status', value: "docked" as String)
				break;
				default:
					sendEvent(name: 'status', value: "error" as String)
				break;
			}
			break;
		}
	}
	else {
		sendEvent(name: 'status', value: "error" as String)
		sendEvent(name: 'network', value: "Not Connected" as String)
		log.debug headerString
	}
	parse
}

// handle commands

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	initialize()
}

def initialize() {
	log.info "Thinking Cleaner ${textVersion()} ${textCopyright()}"
	ipSetup()
	poll()
}

def on() {
	log.debug "Executing 'on'"
	ipSetup()
	api('on')
}

def off() {
	log.debug "Executing 'off'"
	api('off')
}

def spot() {
	log.debug "Executing 'spot'"
	ipSetup()
	api('spot')
}

def poll() {
	log.debug "Executing 'poll'"
    
	if (device.deviceNetworkId != null) {
		api('refresh')
	}
	else {
		sendEvent(name: 'status', value: "error" as String)
		sendEvent(name: 'network', value: "Not Connected" as String)
		log.debug "DNI: Not set"
	}
}

def refresh() {
	log.debug "Executing 'refresh'"
	ipSetup()
	api('refresh')
}

def beep() {
	log.debug "Executing 'beep'"
	ipSetup()
	api('beep')
}

def api(String rooCommand, success = {}) {
	def rooPath
	def hubAction
	if (device.currentValue('network') == "unknown"){
		sendEvent(name: 'network', value: "Not Connected" as String)
		log.debug "Network is not connected"
	}
	else {
		sendEvent(name: 'network', value: "unknown" as String, displayed:false)
	}
	switch (rooCommand) {
		case "on":
			rooPath = "/command.json?command=max"
//            rooPath = "/command.json?command=clean"
			log.debug "The Clean Command was sent"
		break;
		case "off":
			rooPath = "/command.json?command=dock"
			log.debug "The Dock Command was sent"
		break;
		case "spot":
			rooPath = "/command.json?command=spot"
			log.debug "The Spot Command was sent"
		break;
		case "refresh":
			rooPath = "/full_status.json"
			log.debug "The Status Command was sent"
		break;
		case "beep":
			rooPath = "/command.json?command=find_me"
			log.debug "The Beep Command was sent"
		break;
	}
    
	switch (rooCommand) {
		case "refresh":
		case "beep":
			try {
				hubAction = new physicalgraph.device.HubAction(
				method: "GET",
				path: rooPath,
				headers: [HOST: "${settings.ip}:${settings.port}", Accept: "application/json"])
			}
			catch (Exception e) {
				log.debug "Hit Exception $e on $hubAction"
			}
			break;
		default:
			try {
				hubAction = [new physicalgraph.device.HubAction(
				method: "GET",
				path: rooPath,
				headers: [HOST: "${settings.ip}:${settings.port}", Accept: "application/json"]
				), delayAction(9800), api('refresh')]
			}
			catch (Exception e) {
				log.debug "Hit Exception $e on $hubAction"
			}
			break;
	}
	return hubAction
}

def ipSetup() {
	def hosthex
	def porthex
	if (settings.ip) {
		hosthex = convertIPtoHex(settings.ip)
	}
	if (settings.port) {
		porthex = convertPortToHex(settings.port)
	}
	if (settings.ip && settings.port) {
		device.deviceNetworkId = "$hosthex:$porthex"
	}
}

private String convertIPtoHex(ip) { 
	String hexip = ip.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
	return hexip
}
private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
	return hexport
}
private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}