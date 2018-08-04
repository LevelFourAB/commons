module se.l4.commons.types {
	requires io.github.classgraph;

	requires transitive com.fasterxml.classmate;

	requires javassist;
	requires com.google.common;

	exports se.l4.commons.types;
	exports se.l4.commons.types.proxies;
}
