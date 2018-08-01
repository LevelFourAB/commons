module se.l4.commons.types {
	requires io.github.lukehutch.fastclasspathscanner;

	requires transitive com.fasterxml.classmate;

	requires javassist;

	exports se.l4.commons.types;
	exports se.l4.commons.types.proxies;
}
