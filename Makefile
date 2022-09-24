.DEFAULT_GOAL := help
VERSION?=dev-$(shell git log -n 1 --format=%h .)
TOPTARGETS := build push retag
DEPLOYTARGETS := dev local
SUBDIRS = service
.PHONY: $(TOPTARGETS) $(SUBDIRS) $(DEPLOYTARGETS) all-local all help all-build

all-build:
	$(MAKE) build REGISTRY_HOST=localhost:5000
	
all-local: all-build
	$(MAKE) push REGISTRY_HOST=localhost:5000
	$(MAKE) local

$(TOPTARGETS): $(SUBDIRS)
$(SUBDIRS):
	$(MAKE) -C $@ $(MAKECMDGOALS) REGISTRY_HOST=$(REGISTRY_HOST) VERSION=$(VERSION) RELEASE_VERSION=$(RELEASE_VERSION)

$(DEPLOYTARGETS):
	$(MAKE) -C $(MAKECMDGOALS)deploy namespaces services VERSION=$(VERSION)

# Show this help message
help:
	@cat $(MAKEFILE_LIST) | docker run --rm -i xanders/make-help

all: build push