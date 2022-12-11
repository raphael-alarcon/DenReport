package den.reportplugin;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class ReportPlugin extends JavaPlugin implements Listener {

    //region Vault hook
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Permission perms = null;
    private ReportCmd reportCmd;

    /**
     * Method that is called when the plugin is disabled
     * It will save the reports.yml file if it exists.
     */
    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        if (reportCmd.reportsList != null) {
            for (Report r : reportCmd.reportsList) {
                writeReport(r);
            }
        }
    }

    /**
     * Method that is called when the plugin is enabled
     * It will load the reports.yml file if it exists.
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        setupPermissions();
        reportCmd = new ReportCmd(this);
        getCommand("report").setExecutor(reportCmd);
        this.getServer().broadcastMessage("test");
        createCustomConfig();
        getData();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
    }

    public static Permission getPermissions() { return perms; }
    //endregion

    //region Data file
    private File dataFile;
    private FileConfiguration customConfig;

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    /**
     * Method that creates the reports.yml file if it doesn't exist
     */
    private void createCustomConfig() {
        dataFile = new File(getDataFolder(), "reportsList.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveResource("reportsList.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that writes a report to the reports.yml file
     * @param r The report to be written
     */
    public void writeReport(Report r) {
        try {
            this.getCustomConfig().set(r.getId()+".player", r.getReportedPlayer().getName());
            this.getCustomConfig().set(r.getId()+".reason", r.getReason());
            this.getCustomConfig().set(r.getId()+".date", r.getDateOfReport());
            this.getCustomConfig().set(r.getId()+".reportedby", r.getReportingPlayer().getName());
            this.getCustomConfig().save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that gets the data from the reports.yml file
     */
    public void getData() {
        YamlConfiguration data = (YamlConfiguration) this.getCustomConfig();//path to your config file
        for (String id : data.getConfigurationSection("").getKeys(false)) {
            //id will return all different ids of reports
            reportCmd.reportsList.add(new Report( Bukkit.getPlayer(data.getString(id+".player")) , Bukkit.getPlayer(data.getString(id+".reportedby")) , data.getString(id+".reason"), data.getString(id+".date")));
        }
    }

    /**
     * Method that deletes a report from the reports.yml file
     * @param id The report to be deleted
     */
    public void removeData(String id) {
        YamlConfiguration data = (YamlConfiguration) this.getCustomConfig();//path to your config file
        data.set(id, null);
        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    /**
     * Method that is called when a player clicks on an item in an inventory
     * @param e The event that is called
     */
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        if (e.getClickedInventory().getName().equals(ChatColor.translateAlternateColorCodes('&',"&3[Menu] &0Liste des rapports")) && inv.getItem(e.getSlot())!=null) {
            e.setCancelled(true);
            if (!(e.getCurrentItem() == new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5))) {
                int id = Integer.parseInt(inv.getItem(e.getSlot()).getItemMeta().getDisplayName().replace(ChatColor.translateAlternateColorCodes('&',"&cRapport N°"), ""));
                Report selectedReport = getReportById(id);
                if (e.getClick() == ClickType.RIGHT) {
                    if(selectedReport.getReportedPlayer().isOnline()) p.teleport(selectedReport.getReportedPlayer()); else sendError(p,"Joueur hors ligne.");
                } else if (e.getClick() == ClickType.LEFT) {
                    if (selectedReport != null) {
                        reportCmd.reportsList.remove(selectedReport);
                        removeData(selectedReport.getId()+"");
                        ItemStack s = new ItemStack(Material.STAINED_GLASS_PANE);
                        ItemMeta m = s.getItemMeta();
                        m.setDisplayName("§a✔ §7Le rapport à été traité.");
                        s.setItemMeta(m);
                        e.setCurrentItem(s);
                        Bukkit.getScheduler().runTaskLater(this, () -> reportCmd.reportsListGui(p), 3*20);
                    }
                }
            }
        }
    }

    public void sendSuccess(Player p, String s) {
        p.sendTitle("§a✔", "§7" +s);
    }

    public void sendError(Player p, String s) {
        p.sendTitle("§c✘", "§7" +s);
    }

    /**
     * Method that gets a report by its id
     * @param id The id of the report
     * @return The report
     */
    public Report getReportById(int id) {
        for (Report r : reportCmd.reportsList) {
            if (r.getId() == id)return r;
        }
        return null;
    }
}
