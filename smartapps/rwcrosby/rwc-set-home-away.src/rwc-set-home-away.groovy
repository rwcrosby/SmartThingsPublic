/**
 *  RWC Set Home Away
 *
 *  Copyright 2016 Ralph W.  Crosby
 *
 */
definition(
    name: "RWC Set Home Away",
    namespace: "rwcrosby",
    author: "Ralph W.  Crosby",
    description: "Set home or away modes depending on who&#39;s home.",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Who Needs to be Home?") {
		paragraph "Add Presence Sensors"
        input "pSensors", "capability.presenceSensor", required: true, multiple: true
    }	
    
    section("States to Set") {
    	input "someoneHome", "mode", title: "Mode to set when somebody is home"
    	input "allGone", "mode", title: "Mode to set when nobody is home"
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
	subscribe(pSensors, "presence.present", present)
	subscribe(pSensors, "presence.not present", notPresent)
}

def present (event) {
	log.debug "A sensor present event: $event.value, current mode: $location.mode, homemode: $someoneHome, awaymode: $allGone"
    
    // Already home, do nothing
    if (location.mode == someoneHome) {
    	return
    }

	// If the new mode exists, set it
	if (location.modes?.find{it.name == someoneHome}) {
		log.debug "someoneHome mode is valid"
     	setLocationMode(someoneHome)
    }  else {
    	log.warn "Tried to change to undefined mode '${someoneHome}'"
    }

}

def notPresent (event) {
	log.debug "A sensor not present event: $event.value, current mode: $location.mode"

    // Already gone, do nothing
    if (location.mode == allGone) {
    	return
    }
    
    def nobodyHome = true
    for (sensor in pSensors) {
    	if (sensor.currentPresence == "present") {
        	nobodyHome = false
            break
        }
    }
    
    if (nobodyHome) { 
	    log.debug("State change to gone required")

		// If the new mode exists, set it
		if (location.modes?.find{it.name == allGone}) {
			log.debug "allGone mode is valid"
     		setLocationMode(allGone)
    	}  else {
    		log.warn "Tried to change to undefined mode '${allGone}'"
    	}
	}
}