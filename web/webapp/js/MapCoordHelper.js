var MapCoordHelper = function() {
    function gpsNotationReprToNumber(repr, degreeLimit, pos, neg) {
            // split by whitespaces
            var arr = repr.trim().split(/[ ,]+/);
            if (arr.length != 4) {
            	return null;
            }
            
            var degrees = parseInt(arr[0]);
            var minutes = parseInt(arr[1]);
            var seconds = parseFloat(arr[2]);
            var dir = arr[3].toUpperCase();
            
            // check if the arguments are the intended numbers
            if (degrees != arr[0] || minutes != arr[1] || seconds != arr[2]) {
            	return null;
            }
            
            var centiseconds = Math.round(seconds*100);
            
            if (dir != pos && dir != neg) {
            	return null;
            }
            
            var positive = dir == pos;
            
            return ((degrees*60 + minutes)*60*100 + centiseconds) * (positive?1:-1);
    }
    
    function gpsNotationNumberToRepr(num, degreeLimit, pos, neg) {
            num = parseInt(num);
            var abs_total_centiseconds = Math.abs(num);
            var total_minutes = parseInt(abs_total_centiseconds/(60*100));
            var degrees = parseInt(total_minutes/60);
            
            var minutes = total_minutes % 60;
            var centiseconds = (abs_total_centiseconds % (60*100))/100;
            
            var dir = num >= 0 ? pos : neg;
            
            return degrees+' '+minutes+' '+centiseconds+' '+dir;
    }
    
    function latitudeReprToNumber(repr) {
        return gpsNotationReprToNumber(repr, 90, 'N', 'S');
    }
    
    function latitudeNumberToRepr(num) {
        return gpsNotationNumberToRepr(num, 90, 'N', 'S');
    }
    
    function longitudeReprToNumber(repr) {
        return gpsNotationReprToNumber(repr, 180, 'E', 'W');
    }
    
    function longitudeNumberToRepr(num) {
        return gpsNotationNumberToRepr(num, 180, 'E', 'W');
    }
    
    function isAnchorValid(a) {
		return (a !== null && a.gps !== null);
    }
    
    function _getAbridgedAnchorsList(anchors) {
    	var anchor_idx = 0;
    	
    	// returns a valid anchor, or null if there are no more anchors
    	function getNextAnchor() {
    		while (anchor_idx < anchors.length) {
    			var a = anchors[anchor_idx++];
    			if (isAnchorValid(a)) {
    				return a;
    			}
    		}
    		return null;
    	}
    	
    	var anchor0 = getNextAnchor();
    	var anchor1 = getNextAnchor();
    	var anchor2 = getNextAnchor();
    	
    	// oh god why...
    	if (anchor0 === null || anchor1 === null || anchor2 === null) {
    		return [];
    	}
    	
    	return [anchor0, anchor1, anchor2];
    }

    return {
        // Functions to convert GPS representations to numbers and vice-versa
        gpsNumbersToRepr: function(gps) {
            return {lng: longitudeNumberToRepr(gps.lng),
                    lat: latitudeNumberToRepr(gps.lat)};
        },
        gpsReprToNumbers: function(gps) {
            return {lng: longitudeReprToNumber(gps.lng),
                    lat: latitudeReprToNumber(gps.lat)};
        },
        
        /*
            Example anchor data
            
            anchors: [
                {gps: {lng: -2738383, lat: 2393323},  local: {x: 23,  y: 502}},
                {gps: {lng: -4366,    lat: -6546548}, local: {x: 0,   y: 203}},
                {gps: {lng: 67546,    lat: 887643},   local: {x: 424, y: 685}}
            ]
        */
        getImageLocalGPSTransform: function(anchors) {
        	var a = _getAbridgedAnchorsList(anchors);
        	if (a.length < 3) {
        		return null;
        	}
        	
            // GPS coordinates
            var gx0 = a[0].gps.lng;
            var gy0 = a[0].gps.lat;
            var gx1 = a[1].gps.lng;
            var gy1 = a[1].gps.lat;
            var gx2 = a[2].gps.lng;
            var gy2 = a[2].gps.lat;
            
            // Image-local coordinates
            var px0 = a[0].local.x;
            var py0 = a[0].local.y;
            var px1 = a[1].local.x
            var py1 = a[1].local.y;
            var px2 = a[2].local.x;
            var py2 = a[2].local.y;
            
            // Some very complicated formulas churned out by wxMaxima.
            /*
linsolve([
gx0 = m00*px0 + m01*py0 + m02,
gy0 = m10*px0 + m11*py0 + m12,

gx1 = m00*px1 + m01*py1 + m02,
gy1 = m10*px1 + m11*py1 + m12,

gx2 = m00*px2 + m01*py2 + m02,
gy2 = m10*px2 + m11*py2 + m12
],
[m00, m01, m02, m10, m11, m12]);
            */
            var m00=(gx0*(py2-py1)-gx1*py2+gx2*py1+(gx1-gx2)*py0)/(px0*(py2-py1)-px1*py2+px2*py1+(px1-px2)*py0);
            var m01=-(gx0*(px2-px1)-gx1*px2+gx2*px1+(gx1-gx2)*px0)/(px0*(py2-py1)-px1*py2+px2*py1+(px1-px2)*py0);
            var m02=(gx0*(px2*py1-px1*py2)+px0*(gx1*py2-gx2*py1)+(gx2*px1-gx1*px2)*py0)/(px0*(py2-py1)-px1*py2+px2*py1+(px1-px2)*py0);
            var m10=(gy0*(py2-py1)-gy1*py2+gy2*py1+(gy1-gy2)*py0)/(px0*(py2-py1)-px1*py2+px2*py1+(px1-px2)*py0);
            var m11=-(gy0*(px2-px1)-gy1*px2+gy2*px1+(gy1-gy2)*px0)/(px0*(py2-py1)-px1*py2+px2*py1+(px1-px2)*py0);
            var m12=(gy0*(px2*py1-px1*py2)+px0*(gy1*py2-gy2*py1)+(gy2*px1-gy1*px2)*py0)/(px0*(py2-py1)-px1*py2+px2*py1+(px1-px2)*py0);
            
            return {
                imageLocalToGPS: function(x, y) {
                    return {lng: m00*x + m01*y + m02,
                            lat: m10*x + m11*y + m12};
                },
                
                gpsToImageLocal: function(gps) {
                    // More formulas churned out by wxMaxima
                    // It's the inverse of a matrix multiplication
                    var x = (m01*(gps.lat-m12)+m02*m11-gps.lng*m11)/(m01*m10-m00*m11);
                    var y = -(m00*(gps.lat-m12)+m02*m10-gps.lng*m10)/(m01*m10-m00*m11);
                    
                    return {x: Math.round(x),
                            y: Math.round(y)};
                }
            }
        },
        
        isAnchorsListValid: function(anchors) {
        	var a = _getAbridgedAnchorsList(anchors);
        	return a.length >= 3;
        },

        getAbridgedAnchorsList: function(anchors) {
        	return _getAbridgedAnchorsList(anchors);
        }
    };
}();
