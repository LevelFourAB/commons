module se.l4.commons.types {
	requires com.github.spotbugs.annotations;

	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	requires com.github.benmanes.caffeine;

	requires net.bytebuddy;

	requires io.github.classgraph;

	exports se.l4.commons.types;
	exports se.l4.commons.types.conversion;
	exports se.l4.commons.types.mapping;
	exports se.l4.commons.types.matching;
	exports se.l4.commons.types.proxies;
	exports se.l4.commons.types.reflect;
}
