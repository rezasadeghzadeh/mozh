package util

import "math"

// LatLongInDistance calculate min,max lat lng for a distance in km
func LatLongInDistance(lat float64, lng float64, distance int) (float64, float64, float64, float64) {
	r := 6371.009

	maxLat  := lat + Deg(float64(distance)/ r)
	minLat  := lat -  Deg(float64(distance) / r)

	maxLng := lng + Deg(float64(distance) / r / math.Cos( Deg(lat) ))
	minLng := lng - Deg(float64(distance) / r / math.Cos( Deg(lat) ))

	return  minLat,minLng,maxLat,maxLng
}

const x = math.Pi / 180;

func Rad(d float64) float64 {
	return d * x
};

func Deg(r float64) float64 {
	return r / x
}
