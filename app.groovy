/**
 *  StatusThing 
 *
 *  Author: alexking@me.com
 *  Date: 2013-11-01
 * 
 *  Download the Mac App at https://github.com/alexking/StatusThing
 *
 */

definition(
    name: "StatusThing",
    namespace: "",
    author: "alexking@me.com",
    description: "Mac app for controlling your SmartThings from your status bar. See the temperature at a glance, it F° or C°.",
    category: "Convenience",
    iconUrl: "https://alexking.io/StatusThing/SmartApp.png",
    iconX2Url: "https://alexking.io/StatusThing/SmartApp@2x.png",
    oauth: [displayName: "StatusThing", displayLink: ""]
)

preferences {
    section("Settings") {
        input "switches", "capability.switch", title : "Switches", multiple : true, required : true
        input "temperatures", "capability.temperatureMeasurement", title : "Temperature", multiple : true, required : false
        input "thermostats", "capability.thermostat", title : "Thermostats", multiple : true, required : false
		input "contactSensors", "capability.contactSensor", title : "Doors and Windows", multiple : true, required : false
        input "motionSensors", "capability.motionSensor", title : "Motion Sensors", multiple : true, required : false
        input "alarms", "capability.alarm", title : "Alarms", multiple : true, required : false
        input "waterSensors", "capability.waterSensor", title : "Water Sensors", multiple : true, required : false
        input "smokeDetectors", "capability.smokeDetector", title : "Smoke Detectors", multiple : true, required : false
    }
}

mappings 
{
    path("/updateItemsAndTemperature") 
    {
        action : 
        [
            GET : "updateItemsAndTemperatures"
        ] 
    }
    
    path("/itemChangeToState/:id/:state")
    {
        action : 
        [
            GET : "itemChangeToState"
        ]
    }
}


def updateItemsAndTemperatures()
{
    def items = []
    for (item in switches)
    {
        items << [ 'id' : item.id , 'state' : item.currentValue('switch') ?: '' , 'name' : item.displayName ]
    }
    
    def contactSensorItems = []
    if (contactSensors)
    {
 	    for (item in contactSensors)
    	{
    		contactSensorItems << [ 'id' : item.id , 'state' : item.currentValue('contact') ?: '' , 'name' : item.displayName ]
    	}
    }
    
    def motionSensorItems = []
    if (motionSensors)
    {
    	for (item in motionSensors)
        {
        	motionSensorItems << [ 'id' : item.id , 'state' : item.currentValue('motion') ?: '' , 'battery' : item.currentValue('battery') ?: '' , 'name' : item.displayName ]
        }
    }
    
    def alarmState = []
    alarmState << [ 'state' : location.currentValue('alarmSystemStatus') ?: '' , 'name' : 'Alarm' ]
    
    def alarmItems = []
    if (alarms)
    {
    	for (item in alarms)
        {
        	alarmItems << [ 'id' : item.id , 'state' : item.currentValue('alarm') ?: '' , 'name' : item.displayName ]
        }
    }
    
    def waterSensorItems = []
    if (waterSensors)
    {
    	for (item in waterSensors)
        {
        	waterSensorItems << [ 'id' : item.id , 'state' : item.currentValue('water') ?: '' , 'battery' : item.currentValue('battery') ?: '' , 'name' : item.displayName ]
        }
    }
    
    def smokeDetectorItems = []
    if (smokeDetectors)
    {
    	for (item in smokeDetectors)
        {
        	smokeDetectorItems << [ 'id' : item.id , 'smoke' : item.currentValue('smoke') ?: '' , 'battery' : item.currentValue('battery') ?: '' , 'alarmState' : item.currentValue('alarmState') ?: '' , 'name' : item.displayName ]
        }
    }

    def temperatureItems = []
    if (temperatures)
    {
        for (temperature in temperatures)
        {
            def temperatureState = temperature.currentState('temperature')
            
            temperatureItems << [ 
                'id'    : temperature.id, 
                'name'  : temperature.displayName, 
                'value' : temperature.currentValue("temperature").toInteger(), 
                'unit'  : temperatureState.hasProperty('unit') ? temperatureState.unit : 'F'
            ]
        }
    }
    
    if (thermostats)
    {
        for (temperature in thermostats)
        {
            def temperatureState = temperature.currentState('temperature')
            
            if (temperatureState != null) {
                temperatureItems << [ 
                    'id'    : temperature.id, 
                    'name'  : temperature.displayName, 
                    'value' : temperature.currentValue("temperature").toInteger(), 
                    'unit'  : temperatureState.hasProperty('unit') ? temperatureState.unit : 'F'
                ]
            }
            
        }
    }

    [ 'temperatures' : temperatureItems , 'items' : items , 'contactSensors' : contactSensorItems, 'motionSensors' : motionSensorItems, 'alarm' : alarmState, 'alarms' : alarmItems, 'waterSensors' : waterSensorItems , 'smokeDetectors' : smokeDetectorItems ]

}

def itemChangeToState()
{

    def item = switches.find { it.id == params.id }

    if (params.state == "on")
    {
        item.on();
    } else {
        item.off();
    }


    def data = updateItemsAndTemperatures()

    for(dataItem in data['items']) 
    {
        if (dataItem.id == params.id)
        {
            dataItem.state = params.state; 
        }

    }

    data

}

def installed() { }

def updated() { }