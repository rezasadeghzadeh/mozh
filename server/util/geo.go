package util

import "math"


// LatLongInDistance calculate min,max lat lng for a distance in km
func LatLongInDistance(lat float64, lng float64) (float64, float64, float64, float64) {
	// 111 kilometers / 1000 = 111 meters.
	// 1 degree of latitude = ~111 kilometers.
	// 1 / 1000 means an offset of coordinate by 111 meters.

	offset := 27 / 1000.0; //~mean  about 3000 m
	latMax := lat + offset;
	latMin := lat - offset;

	// With longitude, things are a bit more complex.
	// 1 degree of longitude = 111km only at equator (gradually shrinks to zero at the poles)
	// So need to take into account latitude too, using cos(lat).

	lngOffset := offset * math.Cos(lat* math.Pi / 180.0);
	lngMax := lng + lngOffset;
	lngMin := lng - lngOffset;

	return  latMin,lngMin,latMax,lngMax

}
