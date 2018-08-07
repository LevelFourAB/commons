module se.l4.commons.types {
	requires com.github.spotbugs.annotations;

	requires io.github.classgraph;

	requires transitive com.fasterxml.classmate;

	requires net.bytebuddy;
	requires com.google.common;

	exports se.l4.commons.types;
	exports se.l4.commons.types.proxies;
}
