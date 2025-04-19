package org.inventivetalent.reflection.minecraft;

import org.bukkit.entity.Entity;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.resolver.minecraft.OBCClassResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Helper class to access minecraft/bukkit specific objects
 */
public class Minecraft {

    public static final Pattern NUMERIC_VERSION_PATTERN = Pattern.compile("v([0-9])_([0-9]*)_R([0-9])");

    /**
     * @deprecated use {@link MinecraftVersion#VERSION} instead
     */
    @Deprecated(forRemoval = true)
    public static final Version VERSION = Version.UNKNOWN;
    public static final MinecraftVersion MINECRAFT_VERSION = MinecraftVersion.VERSION;

    private static final NMSClassResolver nmsClassResolver = new NMSClassResolver();
    private static final OBCClassResolver obcClassResolver = new OBCClassResolver();
    private static Class<?> NmsEntity = null;
    private static Class<?> CraftEntity = null;

    private static Class<?> getNmsEntity() {
        if (NmsEntity == null) {
            try {
                NmsEntity = nmsClassResolver.resolve("Entity", "world.entity.Entity");
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return NmsEntity;
    }

    private static Class<?> getCraftEntity() {
        if (CraftEntity == null) {
            try {
                CraftEntity = obcClassResolver.resolve("entity.CraftEntity");
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return CraftEntity;
    }

    /**
     * @return the current NMS/OBC version (format <code>&lt;version&gt;.</code>
     */
    @Deprecated(forRemoval = true)
    public static String getVersion() {
        return MINECRAFT_VERSION.packageName() + ".";
    }

    /**
     * @return the current NMS version package
     */
    public static String getNMSPackage() {
        return MINECRAFT_VERSION.getNmsPackage();
    }

    /**
     * @return the current OBC package
     */
    public static String getOBCPackage() {
        return MINECRAFT_VERSION.getObcPackage();
    }

    public static Object getHandle(Object object) throws ReflectiveOperationException {
        Method method;
        try {
            method = AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getHandle"));
        } catch (ReflectiveOperationException e) {
            method = AccessUtil.setAccessible(getCraftEntity().getDeclaredMethod("getHandle"));
        }
        return method.invoke(object);
    }

    public static Entity getBukkitEntity(Object object) throws ReflectiveOperationException {
        Method method;
        try {
            method = AccessUtil.setAccessible(getNmsEntity().getDeclaredMethod("getBukkitEntity"));
        } catch (ReflectiveOperationException e) {
            method = AccessUtil.setAccessible(getCraftEntity().getDeclaredMethod("getHandle"));
        }
        return (Entity) method.invoke(object);
    }

    public static Object getHandleSilent(Object object) {
        try {
            return getHandle(object);
        } catch (Exception ignored) {
        }
        return null;
    }

    public enum Version {
        UNKNOWN(-1) {
            @Override
            public boolean matchesPackageName(String packageName) {
                return false;
            }
        },

        v1_7_R1(10701),
        v1_7_R2(10702),
        v1_7_R3(10703),
        v1_7_R4(10704),

        v1_8_R1(10801),
        v1_8_R2(10802),
        v1_8_R3(10803),
        //Does this even exists?
        v1_8_R4(10804),

        v1_9_R1(10901),
        v1_9_R2(10902),

        v1_10_R1(11001),

        v1_11_R1(11101),

        v1_12_R1(11201),

        v1_13_R1(11301),
        v1_13_R2(11302),

        v1_14_R1(11401),

        v1_15_R1(11501),

        v1_16_R1(11601),
        v1_16_R2(11602),
        v1_16_R3(11603),

        v1_17_R1(11701),

        v1_18_R1(11801),
        v1_18_R2(11802),

        v1_19_R1(11901),
        v1_19_R2(11902),
        v1_19_R3(11904),

        v1_20_R1(12001),
        v1_20_R2(12002),
        v1_20_R3(12003),
        v1_20_R4(12006),
        v1_21_R1(12101),
        v1_21_R2(12102),
        v1_21_R3(12104),

        /// (Potentially) Upcoming versions
        v1_22_R1(12201),
        ;

        private final MinecraftVersion version;

        Version(int version, String nmsFormat, String obcFormat, boolean nmsVersionPrefix) {
            this.version = new MinecraftVersion(name(), version, nmsFormat, obcFormat, nmsVersionPrefix);
        }

        Version(int version) {
            if (version >= 11701) { // 1.17+ new class package name format
                this.version = new MinecraftVersion(name(), version, "net.minecraft", "org.bukkit.craftbukkit.%s", false);
            } else {
                this.version = new MinecraftVersion(name(), version);
            }
        }

        /**
         * @return the version-number
         */
        public int version() {
            return version.version();
        }

        /**
         * @param version the version to check
         * @return <code>true</code> if this version is older than the specified version
         */
        @Deprecated(forRemoval = true)
        public boolean olderThan(Version version) {
            return version() < version.version();
        }

        /**
         * @param version the version to check
         * @return <code>true</code> if this version is newer than the specified version
         */
        @Deprecated(forRemoval = true)
        public boolean newerThan(Version version) {
            return version() >= version.version();
        }

        /**
         * @param oldVersion The older version to check
         * @param newVersion The newer version to check
         * @return <code>true</code> if this version is newer than the oldVersion and older that the newVersion
         */
        @Deprecated(forRemoval = true)
        public boolean inRange(Version oldVersion, Version newVersion) {
            return newerThan(oldVersion) && olderThan(newVersion);
        }

        public boolean matchesPackageName(String packageName) {
            return packageName.toLowerCase().contains(name().toLowerCase());
        }

        /**
         * @return the minecraft version
         */
        public MinecraftVersion minecraft() {
            return version;
        }

        @Override
        public String toString() {
            return name() + " (" + version() + ")";
        }
    }
}
