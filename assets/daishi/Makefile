all: reberu1.json

.PHONY: all clean

clean:
	rm reberu1.json

reberu1.json: reberu1.yaml
	cat $< | yq -j > $@
