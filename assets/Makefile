all: splash.jpg title.jpg images/logo-vi.png images/logo.png

.PHONY: all

clean:
	rm -f splash.jpg
	rm -f title.png
	rm -f images/logo-vi.png
	rm -f images/logo.png

splash.jpg: src/splash.jpg
	convert $< -resize 960x780^ -gravity Center $@

title.png: src/title.png
	convert $< -resize 960x780^ -gravity Center $@

images/logo-vi.png: src/logo-vi.png
	convert $< -resize 260x $@

images/logo.png: src/logo.png
	convert $< -resize x360 $@
