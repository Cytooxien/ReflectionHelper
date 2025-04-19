package org.inventivetalent.reflection.minecraft;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.regex.Matcher;

@Slf4j(topic = "ReflectionHelper/MinecraftVersion")
public class MinecraftVersion {

    public static final MinecraftVersion VERSION;

    static {
        log.info("I am loaded from package {}", Minecraft.class.getPackage().getName());
        try {
            VERSION = MinecraftVersion.getVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get version", e);
        }
        log.info("Version is {}", VERSION);
    }

    private final String packageName;
    private final int version;
    private final String nmsPackage;
    private final String obcPackage;
    private final boolean nmsVersionPrefix;

    MinecraftVersion(String packageName, int version, String nmsFormat, String obcFormat, boolean nmsVersionPrefix) {
        this.packageName = packageName;
        this.version = version;
        this.nmsPackage = String.format(nmsFormat, packageName);
        this.obcPackage = String.format(obcFormat, packageName);
        this.nmsVersionPrefix = nmsVersionPrefix;
    }

    MinecraftVersion(String packageName, int version) {
        this(packageName, version, "net.minecraft.server.%s", "org.bukkit.craftbukkit.%s", true);
    }

    /**
     * @return the version-number
     */
    public int version() {
        return version;
    }

    /**
     * @deprecated use {@link #getNmsPackage()} / {@link #getObcPackage()} instead
     */
    @Deprecated
    public String packageName() {
        return packageName;
    }

    /**
     * @return the full package name for net.minecraft....
     */
    public String getNmsPackage() {
        return nmsPackage;
    }

    /**
     * @return the full package name for org.bukkit....
     */
    public String getObcPackage() {
        return obcPackage;
    }

    /**
     * @return if the nms package name has version prefix
     */
    public boolean hasNMSVersionPrefix() {
        return nmsVersionPrefix;
    }

    /**
     * @param version the version to check
     * @return <code>true</code> if this version is older than the specified version
     */
    public boolean olderThan(Minecraft.Version version) {
        return version() < version.version();
    }

    /**
     * @param version the version to check
     * @return <code>true</code> if this version is equals than the specified version
     */
    public boolean equal(Minecraft.Version version) {
        return version() == version.version();
    }

    /**
     * @param version the version to check
     * @return <code>true</code> if this version is newer than the specified version
     */
    public boolean newerThan(Minecraft.Version version) {
        return version() >= version.version();
    }

    /**
     * @param oldVersion The older version to check
     * @param newVersion The newer version to check
     * @return <code>true</code> if this version is newer than the oldVersion and older that the newVersion
     */
    public boolean inRange(Minecraft.Version oldVersion, Minecraft.Version newVersion) {
        return newerThan(oldVersion) && olderThan(newVersion);
    }

    public boolean matchesPackageName(String packageName) {
        return this.packageName.toLowerCase().contains(packageName.toLowerCase());
    }

    @Override
    public String toString() {
        return packageName + " (" + version() + ")";
    }

    public static MinecraftVersion getVersion() {
        String versionPackage = getVersionPackage();
        Matcher matcher = Minecraft.NUMERIC_VERSION_PATTERN.matcher(versionPackage);
        while (matcher.find()) {
            if (matcher.groupCount() < 3) {
                continue;
            }
            String majorString = matcher.group(1);
            String minorString = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String patchString = String.format("%02d", Integer.parseInt(matcher.group(3)));
            int numVersion = Integer.parseInt(majorString + minorString + patchString);
            String packageName = "v" + versionPackage.substring(1).toUpperCase();

            boolean postOneSeventeen = numVersion > 11701;
            if (postOneSeventeen) { // new nms package format for 1.17+
                return new MinecraftVersion(packageName, numVersion, "net.minecraft", "org.bukkit.craftbukkit.%s", false);
            }
            return new MinecraftVersion(packageName, numVersion);
        }
        log.error("Failed to create dynamic version for {}", versionPackage);

        return new MinecraftVersion("UNKNOWN", -1);
    }

    private static String getVersionPackage() {
        try {
            Class<?> paperMappingEnvironment = Class.forName("io.papermc.paper.util.MappingEnvironment");
            Field fieldCbVersion = paperMappingEnvironment.getField("LEGACY_CB_VERSION");
            return (String) fieldCbVersion.get(null);
        } catch (Exception e) {
            return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
    }
}
