.DEFAULT_GOAL := help

IMAGE=$(REGISTRY_HOST)/tsdloss/service

DOCKER=docker

DOCKER_BUILD_CONTEXT=../
DOCKER_FILE_PATH=./Dockerfile

# Show this help message
help:
	@cat $(MAKEFILE_LIST) | docker run --rm -i xanders/make-help

# Building a Docker Image needs `REGISTRY_HOST` and `VERSION`
build:
	$(DOCKER) build -t $(IMAGE):$(VERSION) $(DOCKER_BUILD_CONTEXT) -f $(DOCKER_FILE_PATH)
	$(DOCKER) tag $(IMAGE):$(VERSION) $(IMAGE):latest

# Pushing a Docker Image needs `REGISTRY_HOST` and `VERSION`
push:
	$(DOCKER) push $(IMAGE):$(VERSION)
	$(DOCKER) push $(IMAGE):latest

# Pushing and retagging Docker Image to `REGISTRY_HOST` from `VERSION` to `RELEASE_VERSION`
retag:
	$(DOCKER) pull $(IMAGE):$(VERSION)
	$(DOCKER) tag $(IMAGE):$(VERSION) $(IMAGE):$(RELEASE_VERSION)
	$(DOCKER) push $(IMAGE):$(RELEASE_VERSION)