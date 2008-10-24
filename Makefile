java:
	$(MAKE) -C src/java

%:
	$(MAKE) -C src/c $@
