/**
 *  Tutorial
 *
 *  Copyright 2016 Ralph W.  Crosby
 *
 */
definition(
    name: "Tutorial",
    namespace: "rwcrosby",
    author: "Ralph W.  Crosby",
    description: "The smartapps developer tutorial",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Turn on when emotion detected") {
		input "themotion", "capability.motionSensor", required: true, title: "Whence?"
	}
    
    section("Wait this long before turning off the light") {
    	input "minutes", "number", required: true, title: "Minutes?"
    }
    
    section("Turn on this light") {
        input "theswitch", "capability.switch", required: true
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
    subscribe(themotion, "motion.active", motionDetectedHandler)
    subscribe(themotion, "motion.inactive", motionStoppedHandler)
}

def motionDetectedHandler(event) {
    log.debug "motionDetectedHandler called $event"
    theswitch.on()
}

def motionStoppedHandler(event) {
    log.debug "motionStoppedHandler called $event"
    runIn minutes, checkInactive
}

def checkInactive() {
	log.debug "checkInactive as scheduled"
		
  	def state = themotion.currentState("motion")
    
    if ( state.value == "inactive") {
    	log.debug "Showing inactive"
        
        def elapsed = now() - state.date.time    // In milliseconds
        def threshold = 1000 * minutes           // In milliseconds
        
        if ( elapsed >= threshold ) {
        	log.debug "Inactive long enough: $elapsed"
            theswitch.off()
        }
        else {
        	log.debug "Not inactive long enough: $elapsed"
        }
        
    }
    else {    
    	log.debug "Showing active"
    }

}