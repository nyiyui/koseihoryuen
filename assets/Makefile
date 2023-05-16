all: splash.jpg title.jpg

.PHONY: all

splash.jpg: src/splash.jpg
	convert $< -resize 960x780^ -gravity Center $@

title.png: src/title.png
	convert $< -resize 960x780^ -gravity Center $@
