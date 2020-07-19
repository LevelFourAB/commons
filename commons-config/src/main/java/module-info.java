module se.l4.commons.config {
	requires com.github.spotbugs.annotations;

	requires transitive se.l4.commons.serialization;
	requires se.l4.commons.io;

	requires transitive java.validation;
	requires transitive org.eclipse.collections.api;

	exports se.l4.commons.config;
	exports se.l4.commons.config.sources;
}
