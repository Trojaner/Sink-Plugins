/*
 * Copyright (c) 2013 adventuria.eu / static-interface.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.static_interface.sinklibrary.configuration;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class LanguageConfiguration
{
    public static final int CURRENT_VERSION = 1;

    private static YamlConfiguration yamlConfiguration = new YamlConfiguration();
    private static HashMap<String, Object> defaultValues;
    private static File yamlFile;

    /**
     * Initialize
     */
    public static void init()
    {
        defaultValues = new HashMap<>();
        create();
    }

    /**
     * Create Configuration
     */
    public static void create()
    {
        try
        {
            yamlFile = new File(SinkLibrary.getCustomDataFolder(), "Language.yml");

            boolean createNewConfiguration = ! exists();

            if (createNewConfiguration)
            {
                Bukkit.getLogger().log(Level.INFO, "Creating new configuration: " + yamlFile);
            }

            if (createNewConfiguration && ! yamlFile.createNewFile())
            {
                Bukkit.getLogger().log(Level.SEVERE, "Couldn't create configuration: " + yamlFile);
                return;
            }

            yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(yamlFile);

            if (! createNewConfiguration)
            {
                int version = (int) get("Main.ConfigVersion");
                if (version < CURRENT_VERSION)
                {
                    Bukkit.getLogger().log(Level.WARNING, "***************");
                    Bukkit.getLogger().log(Level.WARNING, "Configuration: " + yamlFile + " is too old! Current Version: " + version + ", required Version: " + CURRENT_VERSION);
                    recreate();
                    Bukkit.getLogger().log(Level.WARNING, "***************");
                    return;
                }
            }

            if (createNewConfiguration)
            {
                yamlConfiguration.options().copyDefaults(true);
            }
            else
            {
                yamlConfiguration.options().copyDefaults(false);
            }

            addDefault("Main.ConfigVersion", CURRENT_VERSION);
            addDefault("General.NotOnline", "&c%s is not online!");
            addDefault("General.ConsoleNotAvailabe", "&cThis command is only ingame available");

            addDefault("SinkChat.Commands.Nick.OtherChanged", "%s's name is now %s!");
            addDefault("SinkChat.Commands.Nick.SelfChanged", "Your name is now %s!");
            addDefault("SinkChat.Commands.Nick.IllegalNickname", "Illegal nickname!");
            addDefault("SinkChat.Commands.Nick.TooLong", "Nickname is too long!");
            addDefault("SinkChat.Commands.Nick.Used", "Nickname is already used by someone other!");

            addDefault("SinkChat.Commands.Channel.PlayerJoins", "You joined the %s channel.");
            addDefault("SinkChat.Commands.Channel.PlayerLeaves", "You left the %s channel.");
            addDefault("SinkChat.Commands.Channel.NoChannelGiven", "You must write the name of the channel!");
            addDefault("SinkChat.Commands.Channel.ChannelUnknown", "%s is an unknown channel.");
            addDefault("SinkChat.Commands.Channel.List", "These channels are available: %s");
            addDefault("SinkChat.Commands.Channel.Part", "You have the following channels enabled:");
            addDefault("SinkChat.Commands.Channel.Help", "These commands are available:");

            addDefault("SinkChat.Commands.Spy.Enabled", "&aSpy chat has been enabled!");
            addDefault("SinkChat.Commands.Spy.AlreadyEnabled", "&cSpy chat has been already enabled!");

            addDefault("SinkChat.Commands.Spy.Disabled", "&cSpy chat has been disabled!");
            addDefault("SinkChat.Commands.Spy.AlreadyDisabled", "&cSpy chat has been already disabled!");

            addDefault("SinkChat.Channels.Help", "Help");
            addDefault("SinkChat.Channels.Shout", "Shout");
            addDefault("SinkChat.Channels.Trade", "Trade");

            addDefault("SinkChat.Channel.Help.Prefix", "?"); //Todo: move these to Settings
            addDefault("SinkChat.Channel.Shout.Prefix", "!");
            addDefault("SinkChat.Channel.Trade.Prefix", "$");

            addDefault("Permissions.General", "&4You dont have permissions to do that.");
            addDefault("Permissions.SinkChat.Channels.Shout", "&4You may not use the shout channel.");
            addDefault("Permissions.SinkChat.Channels.Help", "&4You may not use the help channel.");
            addDefault("Permissions.SinkChat.Channels.Trade", "&4You may not use the trade channel.");
            addDefault("Permissions.SinkChat.Nick.Other", "&4You may not change the nickname of other players!");

            addDefault("SinkChat.Prefix.Channel", "&a[Channel]");
            addDefault("SinkChat.Prefix.Nick", "&2[Nick]");
            addDefault("SinkChat.Prefix.Spy", "&7[Spy]");
            addDefault("SinkChat.Prefix.Local", "&7[Local]");

            save();
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't create configuration file: " + yamlFile.getName());
            Bukkit.getLogger().log(Level.SEVERE, "Exception occured: ", e);
        }
        catch (InvalidConfigurationException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "***************");
            Bukkit.getLogger().log(Level.SEVERE, "Invalid configuration file detected: " + yamlFile);
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
            Bukkit.getLogger().log(Level.SEVERE, "***************");
            recreate();
        }
    }

    /**
     * Save configuration
     */
    public static void save()
    {
        if (yamlFile == null)
        {
            return;
        }
        if (! exists())
        {
            return;
        }

        try
        {
            yamlConfiguration.save(yamlFile);
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save configuration file: " + yamlFile + "!");
        }
    }

    /**
     * Get Value from path
     *
     * @param path Path to value
     * @return Value of Path
     */
    public static Object get(String path)
    {
        try
        {
            Object value = yamlConfiguration.get(path);
            if (value == null || value == "")
            {
                throw new NullPointerException("Path returned null!");
            }
            return value;
        }
        catch (Exception e)
        {
            if (path.equals("Main.ConfigVersion")) return 0;
            Bukkit.getLogger().log(Level.WARNING, getFile() + ": Couldn't load value from path: " + path + ". Reason: " + e.getMessage() + " Using default value.");
            return getDefault(path);
        }
    }

    /**
     * @return True if file exists
     */
    public static boolean exists()
    {
        return yamlFile.exists();
    }

    /**
     * Delete configuration
     */
    public static void delete()
    {
        yamlFile.delete();
    }

    /**
     * Backup configuration
     */
    public static void backup() throws IOException
    {
        Util.backupFile(getFile(), true);
    }

    /**
     * Get default values
     *
     * @return Default Values
     */
    public static HashMap<String, Object> getDefaults()
    {
        return defaultValues;
    }

    /**
     * Get language as String from key
     *
     * @param path Path to language variable
     * @return Language String
     */
    public static String _(String path)
    {
        try
        {
            String value = yamlConfiguration.getRoot().getString(path);
            if (value == null || value.equals("null"))
            {
                throw new NullPointerException("Path returned null.");
            }
            return ChatColor.translateAlternateColorCodes('&', value);
        }
        catch (Exception e)
        {
            Bukkit.getLogger().log(Level.WARNING, yamlFile.getName() + ": Couldn't load value from path: " + path + ". Reason: " + e.getMessage() + " Using default value.");
            return (String) getDefault(path);
        }
    }

    /**
     * Get Default
     *
     * @param path Path to default value
     * @return Default value
     */
    public static Object getDefault(String path)
    {
        return getDefaults().get(path);
    }

    /**
     * Add default value
     *
     * @param path  Path to default value
     * @param value Value of Path
     */
    public static void addDefault(String path, Object value)
    {
        getDefaults().put(path, value);
        getYamlConfiguration().addDefault(path, value);
    }

    /**
     * Get YAML Configuration
     *
     * @return YamlConfiguration
     */
    public static YamlConfiguration getYamlConfiguration()
    {
        return yamlConfiguration;
    }

    /**
     * Get File
     *
     * @return Configuration File
     */
    public static File getFile()
    {
        return yamlFile;
    }

    /**
     * Recreate configuration
     */
    public static void recreate()
    {
        Bukkit.getLogger().log(Level.WARNING, "Recreating Configuration: " + getFile());
        try
        {
            backup();
        }
        catch (IOException e)
        {
            return;
        }
        delete();
        create();
    }
}