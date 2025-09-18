#!/bin/bash

curl --request POST \
  --url https://fcm.googleapis.com/fcm/send \
  --header 'Authorization: key=AAAAzkSEhHo:APA91bGT-WaPq-bEMy8XPxaWyaRq7Zzcm2qlHLZ7LZUN772krmb1PJZuEyfX3GUpH9WUcBU7o7n2QXldRy3h-Bx8tTDQ_hs8nQzCks9aWG-QAfNyYr2-ne3yw3bXWLbVqBwrhcWerhzl ' \
  --header 'Content-Type: application/json' \
  --data '{
    "to": "dG5ZNISYZE7plulh9dduha:APA91bH6vEJ_2ZlKB2fvFYCtrttn7LjKXuitTBeA23ACQ3ig-A6ngxJWcJILJmqSxWiXQ8jFyP4X7NBcubcsTxMp-PVS56rhei2HPXAp2n0GgZXqZCMSDAKdBPt9yqrPmaRObHyXQyKz",
	"priority": "high",
	"contentAvailable": true,
	"notification": {
		"title": "hola",
		"body": "ajsdajs",
		"sound":"default",
        "image": "https://ruddypazd.com/imagesAdmin/6340999"
	},
    "category":"asd"
}'