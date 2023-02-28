/**
 *  ****************  Fully Kiosk Director App  ****************
 *  Design Usage:
 *  Take control of a Fully Kiosk device!
 *
 *  Copyright 2019-2020 Bryan Turcotte (@bptworld)
 * 
 *  This App is free.  If you like and use this app, please be sure to mention it on the Hubitat forums!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 * 
 *  Unless noted in the code, ALL code contained within this app is mine. You are free to change, ripout, copy, modify or
 *  otherwise use the code in anyway you want. This is a hobby, I'm more than happy to share what I have learned and help
 *  the community grow. Have FUN with it!
 * 
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  3.0.0 - 02/27/23 - new ownership
 *  2.0.3 - 09/02/20 - Cosmetic changes
 *  2.0.2 - 07/31/20 - Adjustments to Time restriction, added mode and lux to triggers
 *  2.0.1 - 04/27/20 - Cosmetic changes
 *  2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  1.0.3 - 06/08/19 - Added Time Delay to triggers
 *  1.0.2 - 05/20/19 - Added Time to triggers
 *  1.0.1 - 05/08/19 - Fixed an issue with loadStartURL, added a delay between commands
 *  1.0.0 - 05/07/19 - Initial release.
 *
 */

import groovy.time.TimeCategory
import java.text.SimpleDateFormat

def setVersion(){
    state.name = "Fully Kiosk Director"
	state.version = "3.0.0"
}

definition(
    name: "Fully Kiosk Director Child",
    namespace: "thegettys",
    author: "Michael Gettys",
    description: "Take control of a Fully Kiosk device!",
    category: "",
	parent: "thegettys:Fully Kiosk Director",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/thegettys/hubitat-fully-kiosk/master/apps/fully-kiosk-app-child.groovy",
)

preferences {
    page(name: "pageConfig")
	page name: "pushOptions", title: "", install: false, uninstall: true, nextPage: "pageConfig"
}

def pageConfig() {
    dynamicPage(name: "", title: "", install: true, uninstall: true, refreshInterval:0) {
		display() 
        section("Instructions:", hideable: true, hidden: true) {
			paragraph "<b>Notes:</b>"
			paragraph "* Vitual Device must use the <a href='https://github.com/thegettys/hubitat-fully-kiosk/blob/master/drivers/FullyKioskBrowserController.groovy' target='_blank'>Fully Kiosk Browser Controller</a> driver"
			paragraph "To be honest, I don't use many of the features available here. But they are in the driver, so I made them available.  Please visit the driver post listed above for more details on what each option does and how to use them.  Thanks!"
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Fully Kiosk Device")) {
			input "fullyDevice", "capability.actuator", title: "Virtual Device created with Fully Kiosk Browser Controller", required: true, multiple: true
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Triggers")) {
			input "myContact", "capability.contactSensor", title: "Select the contact sensor to activate the event", required: false, multiple: true
			input "myMotion", "capability.motionSensor", title: "Select the motion sensor to activate the event", required: false, multiple: true
			input "mySwitch", "capability.switch", title: "Select the switch to activate the event", required: false, multiple: true
            input "myMode", "mode", title: "Select the mode to activate the event", multiple: true
            input "luxRestriction", "bool", title: "Use Lux Restriction", description: "lux", defaultValue:false, submitOnChange:true   
            if(luxRestriction) {
                input "lightSensor", "capability.illuminanceMeasurement", title: "Considered Dark only when illuminance on this light sensor..."
                input "lightLevel", "number", title: "...is equal to or below this illuminance level"
            } else {
                app.removeSetting("lightSensor")
                app.removeSetting("lightLevel")    
            }
			input "timeToRun", "time", title: "Select time to activate the event", required: false
			input "timeDelay", "number", title: "Every X Minutes (1 to 60)", required: false, range: '1..60'
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" BEFORE Display Options")) {
			input(name: "optBringFullyToFront1", type: "bool", defaultValue: "false", title: "Bring Fully to Front?", description: "bring to front", submitOnChange: "true", width: 6)
			input(name: "optLoadStartURL1", type: "bool", defaultValue: "false", title: "Load Start URL?", description: "start URL", submitOnChange: "true", width: 6)
			input(name: "optStartScreensaver1", type: "bool", defaultValue: "false", title: "Start Screensaver?", description: "start screensaver", submitOnChange: "true", width: 6)
			input(name: "optStopScreensaver1", type: "bool", defaultValue: "false", title: "Stop Screensaver?", description: "stop screensaver", submitOnChange: "true", width: 6)
			input(name: "optScreenOn1", type: "bool", defaultValue: "false", title: "Screen On?", description: "screen on", submitOnChange: "true", width: 6)
			input(name: "optScreenOff1", type: "bool", defaultValue: "false", title: "Screen Off?", description: "screen off", submitOnChange: "true", width: 6)
			input "optSetVolume1", "number", title: "Set Volume", required: false, width: 12
			input(name: "optVolumeUp1", type: "bool", defaultValue: "false", title: "Volume Up?", description: "volume up", submitOnChange: "true", width: 6)
			input(name: "optVolumeDown1", type: "bool", defaultValue: "false", title: "Volume Down?", description: "volume down", submitOnChange: "true", width: 6)
			input(name: "optMute1", type: "bool", defaultValue: "false", title: "Mute?", description: "mute", submitOnChange: "true", width: 6)
			input(name: "optUnmute1", type: "bool", defaultValue: "false", title: "Unmute?", description: "unmute", submitOnChange: "true", width: 6)
			input "optPlaySound1", "text", title: "Play Sound", required: false, width: 6
			input(name: "optStopSound1", type: "bool", defaultValue: "false", title: "Stop Sound?", description: "stop sound", submitOnChange: "true", width: 6)
			input "optSpeak1", "text", title: "Speak", required: false, width: 12
			input(name: "optSiren1", type: "bool", defaultValue: "false", title: "Siren?", description: "siren", submitOnChange: "true", width: 6)
			input(name: "optStrobe1", type: "bool", defaultValue: "false", title: "Strobe?", description: "strobe", submitOnChange: "true", width: 6)
			input "optSetScreenBrightness1", "number", title: "Set Screen Brightness", required: false, width: 6
			input(name: "optTriggerMotion1", type: "bool", defaultValue: "false", title: "Trigger Motion?", description: "trigger motion", submitOnChange: "true", width: 6)
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Display Options")) {
			paragraph "<b>Please, only select ONE of the following options</b>"
			input "optLaunchAppPackage", "text", title: "option: Launch App Package", required: false, submitOnChange: true
			input "optLoadURL", "text", title: "option: Load URL", required: false, submitOnChange: true
			if(optLaunchAppPackage && optLoadURL) {
				paragraph "<b>ONLY SELECT ONE OPTION!</b>"
			} else {
				paragraph ""
			}
			if(optLaunchAppPackage || optLoadURL) {
				paragraph "Go back to Fully after a certain amount of time or when trigger ends?"
				input(name: "triggerORTime", type: "bool", defaultValue: "false", title: "Trigger (off) or Time (on)", description: "trigger OR Time", submitOnChange: "true")
				if(triggerORTime) input "pTime", "number", title: "Wait how long before automaticaly going back to Fully? (in seconds)", required: true
			}
		}
		
		section(getFormat("header-green", "${getImage("Blank")}"+" AFTER Display Options")) {
			input(name: "optBringFullyToFront2", type: "bool", defaultValue: "false", title: "Bring Fully to Front?", description: "bring to front", submitOnChange: "true", width: 6)
			input(name: "optLoadStartURL2", type: "bool", defaultValue: "false", title: "Load Start URL?", description: "start URL", submitOnChange: "true", width: 6)
			input(name: "optStartScreensaver2", type: "bool", defaultValue: "false", title: "Start Screensaver?", description: "start screensaver", submitOnChange: "true", width: 6)
			input(name: "optStopScreensaver2", type: "bool", defaultValue: "false", title: "Stop Screensaver?", description: "stop screensaver", submitOnChange: "true", width: 6)
			input(name: "optScreenOn2", type: "bool", defaultValue: "false", title: "Screen On?", description: "screen on", submitOnChange: "true", width: 6)
			input(name: "optScreenOff2", type: "bool", defaultValue: "false", title: "Screen Off?", description: "screen off", submitOnChange: "true", width: 6)
			input "optSetVolume2", "number", title: "Set Volume", required: false, width: 12
			input(name: "optVolumeUp2", type: "bool", defaultValue: "false", title: "Volume Up?", description: "volume up", submitOnChange: "true", width: 6)
			input(name: "optVolumeDown2", type: "bool", defaultValue: "false", title: "Volume Down?", description: "volume down", submitOnChange: "true", width: 6)
			input(name: "optMute2", type: "bool", defaultValue: "false", title: "Mute?", description: "mute", submitOnChange: "true", width: 6)
			input(name: "optUnmute2", type: "bool", defaultValue: "false", title: "Unmute?", description: "unmute", submitOnChange: "true", width: 6)
			input "optPlaySound2", "text", title: "Play Sound", required: false, width: 6
			input(name: "optStopSound2", type: "bool", defaultValue: "false", title: "Stop Sound?", description: "stop sound", submitOnChange: "true", width: 6)
			input "optSpeak2", "text", title: "Speak", required: false, width: 12
			input(name: "optSiren2", type: "bool", defaultValue: "false", title: "Siren?", description: "siren", submitOnChange: "true", width: 6)
			input(name: "optStrobe2", type: "bool", defaultValue: "false", title: "Strobe?", description: "strobe", submitOnChange: "true", width: 6)
			input "optSetScreenBrightness2", "number", title: "Set Screen Brightness", required: false, width: 6
			input(name: "optTriggerMotion2", type: "bool", defaultValue: "false", title: "Trigger Motion?", description: "trigger motion", submitOnChange: "true", width: 6)
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Allow to run between what times? (Optional)")) {
        	input "fromTime", "time", title: "From", required: false
        	input "toTime", "time", title: "To", required: false
            input "midnightCheckR", "bool", title: "Does this time frame cross over midnight", defaultValue:false, submitOnChange:true
		}
        
        section(getFormat("header-green", "${getImage("Blank")}"+" App Control")) {
            input "pauseApp", "bool", title: "Pause App", defaultValue:false, submitOnChange:true            
            if(pauseApp) {
                if(app.label) {
                    if(!app.label.contains(" (Paused)")) {
                        app.updateLabel(app.label + " (Paused)")
                    }
                }
            } else {
                if(app.label) {
                    app.updateLabel(app.label - " (Paused)")
                }
            }
            paragraph "This app can be enabled/disabled by using a switch. The switch can also be used to enable/disable several apps at the same time."
            input "disableSwitch", "capability.switch", title: "Switch Device(s) to Enable / Disable this app", submitOnChange:true, required:false, multiple:true
        }
        
		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {
            label title: "Enter a name for this automation", required: false
            input "logEnable", "bool", defaultValue:false, title: "Enable Debug Logging", description: "Enable extra logging for debugging."
		}
		display2()
	}
}
			
def installed() {
    log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {	
    if(logEnable) log.debug "Updated with settings: ${settings}"
    unsubscribe()
    if(logEnable) runIn(3600, logsOff)
	initialize()
}

def initialize() {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        setDefaults()
        if(myContact) subscribe(myContact, "contact", contactSensorHandler)
        if(lightSensor) subscribe(lightSensor, "illuminance", luxLevelHandler)
        if(myMode) subscribe(location, "mode", modeHandler)
        if(myMotion) subscribe(myMotion, "motion", motionSensorHandler)
        if(mySwitch) subscribe(mySwitch, "switch", switchHandler)
        if(timeToRun) schedule(timeToRun, timeHandler)
        if(timeDelay) runIn(timeDelay,timeDelayHandler)
    }
}

def contactSensorHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "In contactSensorHandler..."
        state.contactStatus = evt.value
        if(state.contactStatus == "open") {
            if(logEnable) log.debug "In contactSensorHandler - open"
            beginHandler()
            if(triggerORTime) runIn(pTime,endHandler)
        } else {
            if(logEnable) log.debug "In contactSensorHandler - closed"
            if(!triggerORTime) endHandler()
        }
    }
}

def modeHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "In modeHandler..."
        state.modeStatus = evt.value
        if(myMode.contains(location.mode)) {
            if(logEnable) log.debug "In modeHandler - mode is active"
            beginHandler()
            if(triggerORTime) runIn(pTime,endHandler)
        } else {
            if(logEnable) log.debug "In modeHandler - mode is Not active"
            if(!triggerORTime) endHandler()
        }
    }
}                                               
                         
def motionSensorHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "In motionSensorHandler..."
        state.motionStatus = evt.value
        if(state.motionStatus == "active") {
            if(logEnable) log.debug "In motionSensorHandler - active"
            beginHandler()
            if(triggerORTime) runIn(pTime,endHandler)
        } else {
            if(logEnable) log.debug "In motionSensorHandler - Not active"
            if(!triggerORTime) endHandler()
        }
    }
}

def switchHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "In switchHandler..."
        state.switchStatus = evt.value
        if(state.switchStatus == "on") {
            if(logEnable) log.debug "In switchHandler - on"
            beginHandler()
            if(triggerORTime) runIn(pTime,endHandler)
        } else {
            if(logEnable) log.debug "In switchHandler - off"
            if(!triggerORTime) endHandler()
        }
    }
}
			
def luxLevelHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "In luxLevelHandler (${state.version})"
        if(lightSensor != null) {
            if(lightLevel == null) {lightLevel = 0}
            state.curLev = lightSensor.currentValue("illuminance").toInteger()
            if (state.curLev >= lightLevel.toInteger()) {
                if(logEnable) log.debug "In luxLevelHandler...Current Light Level: ${state.curLev} is greater than lightValue: ${lightLevel} - isItDark: false"
                state.isItDark = false
                if(!triggerORTime) endHandler()
            } else {
                if(logEnable) log.debug "In luxLevelHandler...Current Light Level: ${state.curLev} is less than lightValue: ${lightLevel} - isItDark: true"
                state.isItDark = true
                beginHandler()
                if(triggerORTime) runIn(pTime,endHandler)
            }
        }
    }
}

def timeHandler() {
	if(logEnable) log.debug "In timeHandler..."
	beginHandler()
	if(triggerORTime) runIn(pTime,endHandler)
}

def timeDelayHandler() {
	if(logEnable) log.debug "In timeDelayHandler..."
	def runDelay = timeDelay * 60
	beginHandler()
	runIn(runDelay,timeDelayHandler)
}

def beginHandler() {
	checkTime()
	thePause = 1000
	if(state.timeBetween == true) {
		if(logEnable) log.debug "In beginHandler - pause between commands: ${thePause}"
			if(optLoadStartURL1) {
				fullyDevice.loadStartURL()
				pauseExecution(thePause)
			}
			if(optBringFullyToFront1) {
				fullyDevice.bringFullyToFront()
				pauseExecution(thePause)
			}
			if(optStartScreensaver1) {
				fullyDevice.startScreensaver()
				pauseExecution(thePause)
			}
			if(optStopScreensaver1) {
				fullyDevice.stopScreensaver()
				pauseExecution(thePause)
			}
			if(optScreenOn1) {
				fullyDevice.screenOn()
				pauseExecution(thePause)
			}
			if(optScreenOff1) {
				fullyDevice.screenOff()
				pauseExecution(thePause)
			}
			if(optSetVolume1) {
				fullyDevice.setVolume(optSetVolume1)
				pauseExecution(thePause)
			}
			if(optVolumeUp1) {
				fullyDevice.volumeUp()
				pauseExecution(thePause)
			}
			if(optVolumeDown1) {
				fullyDevice.volumeDown()
				pauseExecution(thePause)
			}
			if(optMute1) {
				fullyDevice.mute()
				pauseExecution(thePause)
			}
			if(optUnmute1) {
				fullyDevice.unmute()
				pauseExecution(thePause)
			}
			if(optPlaySound1) {
				fullyDevice.playSound(optPlaySound1)
				pauseExecution(thePause)
			}
			if(optStopSound1) {
				fullyDevice.stopSound()
				pauseExecution(thePause)
			}
			if(optSpeak1) {
				fullyDevice.speak(optSpeak1)
				pauseExecution(thePause)
			}
			if(optSiren1) {
				fullyDevice.siren()
				pauseExecution(thePause)
			}
			if(optStrobe1) {
				fullyDevice.strobe()
				pauseExecution(thePause)
			}
			if(optSetScreenBrightness1) {
				fullyDevice.setScreenBrightness(optSetScreenBrightness1)
				pauseExecution(thePause)
			}
			if(optTriggerMotion1) {
				fullyDevice.triggerMotion()
				pauseExecution(thePause)
			}
			
			if(optLaunchAppPackage) {
				fullyDevice.launchAppPackage(optLaunchAppPackage)
				pauseExecution(thePause)
			}
			if(optLoadURL) {
				fullyDevice.loadURL(optLoadURL)
				pauseExecution(thePause)
			}
	} else {
		if(logEnable) log.debug "In launchAppHandler - Not allowed at this time"
	}	
}

def endHandler() {
	checkTime()
	thePause = 1000
	if(state.timeBetween == true) {
		if(logEnable) log.debug "In endHandler - pause between commands: ${thePause}"
			if(optLoadStartURL2) {
				fullyDevice.loadStartURL()
				pauseExecution(thePause)
			}
			if(optBringFullyToFront2) {
				fullyDevice.bringFullyToFront()
				pauseExecution(thePause)
			}
			if(optStartScreensaver2) {
				fullyDevice.startScreensaver()
				pauseExecution(thePause)
			}
			if(optStopScreensaver2) {
				fullyDevice.stopScreensaver()
				pauseExecution(thePause)
			}
			if(optScreenOn2) {
				fullyDevice.screenOn()
				pauseExecution(thePause)
			}
			if(optScreenOff2) {
				fullyDevice.screenOff()
				pauseExecution(thePause)
			}
			if(optSetVolume2) {
				fullyDevice.setVolume(optSetVolume2)
				pauseExecution(thePause)
			}
			if(optVolumeUp2) {
				fullyDevice.volumeUp()
				pauseExecution(thePause)
			}
			if(optVolumeDown2) {
				fullyDevice.volumeDown()
				pauseExecution(thePause)
			}
			if(optMute2) {
				fullyDevice.mute()
				pauseExecution(thePause)
			}
			if(optUnmute2) {
				fullyDevice.unmute()
				pauseExecution(thePause)
			}
			if(optPlaySound2) {
				fullyDevice.playSound(optPlaySound2)
				pauseExecution(thePause)
			}
			if(optStopSound2) {
				fullyDevice.stopSound()
				pauseExecution(thePause)
			}
			if(optSpeak2) {
				fullyDevice.speak(optSpeak2)
				pauseExecution(thePause)
			}
			if(optSiren2) {
				fullyDevice.siren()
				pauseExecution(thePause)
			}
			if(optStrobe2) {
				fullyDevice.strobe()
				pauseExecution(thePause)
			}
			if(optSetScreenBrightness2) {
				fullyDevice.setScreenBrightness(optSetScreenBrightness2)
				pauseExecution(thePause)
			}
			if(optTriggerMotion2) {
				fullyDevice.triggerMotion()
				pauseExecution(thePause)
			}
	} else {
		if(logEnable) log.debug "In bringFullyToFrontHandler - Not allowed at this time"
	}	
}

def checkTime() {
	if(logEnable) log.debug "In checkTime (${state.version}) - ${fromTime} - ${toTime}"
	if(fromTime) {
        if(midnightCheckR) {
            state.betweenTime = timeOfDayIsBetween(toDateTime(fromTime), toDateTime(toTime)+1, new Date(), location.timeZone)
        } else {
		    state.betweenTime = timeOfDayIsBetween(toDateTime(fromTime), toDateTime(toTime), new Date(), location.timeZone)
        }
		if(state.betweenTime) {
            if(logEnable) log.debug "In checkTime - Time within range - Don't Speak"
			state.timeBetween = true
		} else {
            if(logEnable) log.debug "In checkTime - Time outside of range - Can Speak"
			state.timeBetween = false
		}
  	} else {  
        if(logEnable) log.debug "In checkTime - NO Time Restriction Specified"
		state.timeBetween = true
  	}
	if(logEnable) log.debug "In checkTime - timeBetween: ${state.timeBetween}"
}

// ********** Normal Stuff **********

def logsOff() {
    log.info "${app.label} - Debug logging auto disabled"
    app?.updateSetting("logEnable",[value:"false",type:"bool"])
}

def checkEnableHandler() {
    state.eSwitch = false
    if(disableSwitch) { 
        if(logEnable) log.debug "In checkEnableHandler - disableSwitch: ${disableSwitch}"
        disableSwitch.each { it ->
            eSwitch = it.currentValue("switch")
            if(eSwitch == "on") { state.eSwitch = true }
        }
    }
}

def setDefaults(){
	if(logEnable) log.debug "In setDefaults..."
	if(logEnable == null){logEnable = false}
}

def getImage(type) {					// Modified from @Stephack Code
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
    if(type == "checkMarkGreen") return "${loc}checkMarkGreen2.png height=30 width=30>"
    if(type == "optionsGreen") return "${loc}options-green.png height=30 width=30>"
    if(type == "optionsRed") return "${loc}options-red.png height=30 width=30>"
    if(type == "instructions") return "${loc}instructions.png height=30 width=30>"
    if(type == "logo") return "${loc}logo.png height=60>"
}

def getFormat(type, myText="") {			// Modified from @Stephack Code   
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "<hr style='background-color:#1A77C9; height: 1px; border: 0;'>"
    if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def display() {
    setVersion()
    theName = app.label
    if(theName == null || theName == "") theName = "New Child App"
    section (getFormat("title", "${state.name} - ${theName}")) {
		paragraph getFormat("line")
	}
}

def display2() {
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center;font-size:20px;font-weight:bold'>${state.name} - ${state.version}</div>"
	}       
}
