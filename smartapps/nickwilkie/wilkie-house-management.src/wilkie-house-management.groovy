/**
 *  Wilkie House Management
 *
 *  Copyright 2015 Nick Wilkie
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
 */
definition(
    name: "Wilkie House Management",
    namespace: "nickwilkie",
    author: "Nick Wilkie",
    description: "Remote management of house stuff",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "Wilkie Apartment Management", displayLink: ""])


preferences {
	section("Components") {
    	input "switches", "capability.switch", title: "Which Switches?", multiple: true, required: false
	}
	section("Movie") {
    	input "movieSwitch", "capability.switch", title: "Movie switch:", multiple: false, required: false
    	input "movieSwitchesRGBW", "capability.colorControl", title: "Movie RGBW:", multiple: true, required: false
        input "movieSwitchesToDim", "capability.switchLevel", title: "Switches to dim:", multiple: true, required: false
        input "movieSwitchesToTurnOn", "capability.switch", title: "Switches to turn on:", multiple: true, required: false
        input "movieSwitchesToTurnOff", "capability.switch", title: "Switches to turn off:", multiple: true, required: false
    }
    
	section("Living Room Bright") {
    	input "lrBrightSwitch", "capability.switch", title: "Living Room Bright switch:", multiple: false, required: false
    	input "lrBrightSwitchesRGBW", "capability.colorControl", title: "Living Room Bright RGBW:", multiple: true, required: false
        input "lrBrightSwitchesToDim", "capability.switchLevel", title: "Switches to dim:", multiple: true, required: false
        input "lrBrightSwitchesToTurnOn", "capability.switch", title: "Switches to turn on:", multiple: true, required: false
        input "lrBrightSwitchesToTurnOff", "capability.switch", title: "Switches to turn off:", multiple: true, required: false
    }
}

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches",
      PUT: "updateSwitches"
    ]
  }
  path("/switches/:id") {
    action: [
      GET: "showSwitch",
      PUT: "updateSwitch"
    ]
  }
}

void updateSwitch() {
    def command = request.JSON?.command
    if (command) {
      def mySwitch = switches.find { it.id == params.id }
      if (!mySwitch) {
        httpError(404, "Switch not found")
      } else {
        mySwitch."$command"()
      }
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(movieSwitch, "switch.on", "movieMode")
	subscribe(lrBrightSwitch, "switch.on", "lrBrightMode")
}

def movieMode(evt) {
	log.debug "Wilkie house management: Movie Mode"
    movieSwitch?.off()
	movieSwitchesRGBW?.movieScene()
    movieSwitchesToDim?.setLevel(15)
    movieSwitchesToTurnOn?.on()
    movieSwitchesToTurnOff?.off()
}

def lrBrightMode(evt) {
	log.debug "Wilkie house management: Living room bright Mode"
    lrBrightSwitch?.off()
	lrBrightSwitchesRGBW?.warmLightScene()
    lrBrightSwitchesToDim?.setLevel(80)
    lrBrightSwitchesToTurnOff?.off()
    lrBrightSwitchesToTurnOn?.on()
}