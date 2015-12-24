/**
 *  RefreshLights
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
    name: "Scheduled Refresh",
    namespace: "nickwilkie",
    author: "Nick Wilkie",
    description: "Refresh the lights in the house so timers and stuff work",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Devices") {
		input(name: "refreshables", type: "capability.refresh", title: "Devices to refresh", multiple: true)
	}
}

def installed() {
	log.debug "Scheduled With Refresh... Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Scheduled With Refresh... Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "Scheduled With Refresh... initialize"
    unschedule()
    schedule("0 * * * * ?", refreshRefreshables)
    schedule("* * 0 * * ?", initialize)
}

def refreshRefreshables() {
	log.debug "Scheduled With Refresh... refreshRefreshables"
	refreshables.refresh()
}