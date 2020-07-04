module se.l4.commons.types {
	requires com.github.spotbugs.annotations;

	requires io.github.classgraph;

	requires net.bytebuddy;
	requires com.google.common;

	exports se.l4.commons.types;
	exports se.l4.commons.types.proxies;
	exports se.l4.commons.types.conversion;
	exports se.l4.commons.types.matching;
	exports se.l4.commons.types.reflect;
}
