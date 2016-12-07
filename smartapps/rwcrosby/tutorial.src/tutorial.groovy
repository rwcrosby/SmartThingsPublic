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
}

def motionDetectedHandler(event) {
    log.debug "motiondetectedHandler called $event"
    theswitch.on()
}