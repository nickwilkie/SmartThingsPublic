x/**
 *  RGBW Dimmer Switch
 *
 *  Copyright 2014 Nick Wilkie
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
    name: "RGBW Dimmer Switch",
    namespace: "nickwilkie",
    author: "Nick Wilkie",
    description: "Control an RGBW controller from dimmer switch",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "RGBW Control", displayLink: ""])


preferences {
	section("Use this switch...") {
		input "master", "capability.switch", multiple: false, required: true
	}
	section("...to control this RGBW controller") {
		input "rgbw", "capability.switch", multiple: false, required: true
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
	subscribe(master, "switch", switchHandler, [filterEvents: false])
	subscribe(master, "level", switchHandler, [filterEvents: false])
	subscribe(rgbw, "switch", rgbwHandler, [filterEvents: false])
	subscribe(rgbw, "scene", rgbwHandler, [filterEvents: false])
}

def switchHandler(evt) {
	log.info "switchHandler($evt.name: $evt.value, type: '${evt.event.type}', isPhysical: ${evt.isPhysical()} (${evt.event.isPhysical()}), isDigital: ${evt.isDigital()})"
    //log.info evt.event.encodeAsJSON()
    
    

	if (evt.isPhysical()) {
    	if (evt.name == "switch" && evt.value != rgbw.currentSwitch){
            if (evt.value == "on") {
                log.debug "switch on detected, rgbw switch is ${rgbw.currentState('switch').value}. Dimmer level is ${master.currentState('level').integerValue}. RGBW scene is ${rgbw.currentState('scene').value}."
                rgbw.on()
            } else if (evt.value == "off") {
                log.debug "switch off detected"
                rgbw.off()
            }
        } else if (evt.name == "level" && master.currentState("level").integerValue > 0) {
            def scenes = [
                    "blue",
                    "green",
                    "purple",
                    "red",
                    "movie",
                    "dimLight",
                    "warmLight"
            ]

            def sceneIdx = Math.floor(((master.currentState("level").integerValue - 1) * scenes.size()) / 98) as Integer //98 denominator works for 7 LEDs
            sceneIdx = Math.min(sceneIdx, 6)
            sceneIdx = Math.max(sceneIdx, 0)
            
            def scene = scenes[sceneIdx]
            if (rgbw.currentState('scene').value == scene) {
                log.info "Not changing scene, scene already set to ${rgbw.currentState('scene').value}"
            } else {
                log.info "Changing scene to $scene"
                rgbw."${scene}Scene"()
            }
        }
	} else {
		log.trace "Skipping digital on/off event"
	}
}

def rgbwHandler(evt) {
	log.info "rgbwHandler($evt.name: $evt.value, type: '${evt.event.type}', isPhysical: ${evt.isPhysical()} (${evt.event.isPhysical()}), isDigital: ${evt.isDigital()})"
    //log.info evt.event.encodeAsJSON()
	
    if (evt.name =="switch" && evt.value != master.currentState('switch') ) {
        if (evt.value == "on") {
            log.debug "rgbw on detected"
            master.on()
        } else if (evt.value == "off") {
            log.debug "rgbw off detected"
            master.off()
        }
    } else if (evt.name == "scene") {
    	log.debug "scene change detected, rgbw scene is ${rgbw.currentScene}. Dimmer level is ${master.currentState('level').integerValue}."
            
        def scenes = [
            "blue",
            "green",
            "purple",
            "red",
            "movie",
            "dimLight",
            "warmLight"
        ]

        
        def sceneIdx = scenes.lastIndexOf(evt.value)
        
        sceneIdx = Math.min(sceneIdx, 6)
        sceneIdx = Math.max(sceneIdx, 0)
        
        if (master.currentSwitch == "on" && master.currentLevel > 0) {
            master.setLevel(sceneIdx*14 + 7)
        } else {
        	log.debug "Master switch off, not changing level"
        }
    }
}