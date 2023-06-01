all: reberu1.json reberu2.json reberu3.json

.PHONY: all clean

clean:
	rm reberu1.json
	rm reberu2.json
	rm reberu3.json

reberu1.json: reberu1.yaml
	cat $< | yq -j > $@

reberu2.json: reberu2.yaml
	cat $< | yq -j > $@

reberu3.json: reberu3.yaml
	cat $< | yq -j > $@
