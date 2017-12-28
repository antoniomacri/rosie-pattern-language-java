# Make shared library

all: libexample.so

libexample.so: example.o
	$(CC) -o $@ -shared example.o -lm

%.o: %.c example.h
	$(CC) -fPIC -c -o $@ $*.c -Wall

clean:
	rm -f libexample.so example.o
