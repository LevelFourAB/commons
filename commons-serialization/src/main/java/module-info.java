module se.l4.commons.serialization {
	requires com.github.spotbugs.annotations;

	requires transitive se.l4.commons.types;
	requires transitive se.l4.commons.io;

	requires com.google.common;

	exports se.l4.commons.serialization;
	exports se.l4.commons.serialization.collections;
	exports se.l4.commons.serialization.enums;
	exports se.l4.commons.serialization.format;
	exports se.l4.commons.serialization.standard;
}
