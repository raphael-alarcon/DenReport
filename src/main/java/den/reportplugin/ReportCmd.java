package den.reportplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ReportCmd implements CommandExecutor {

    //region Private attributes
    ReportPlugin reportPl;
    ArrayList<Report> reportsList = new ArrayList<>();
    //endregion

    /**
     * Constructor for the ReportCmd class
     * @param reportPl The ReportPlugin instance
     */
    public ReportCmd(ReportPlugin reportPl) {
        this.reportPl = reportPl;
    }


    //region Command

    /**
     * Method that is called when the command is executed
     * @param sender The player that executed the command
     * @param cmd The command that was executed
     * @param label The label of the command
     * @param args The arguments of the command
     * @return True if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("report")) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 0) {
                    reportPl.sendError(p, "Arguments manquants.");
                    return true;
                }
                if (args.length < 2) {
                    if (args[0].equalsIgnoreCase("list")) {
                        reportsListGui(p);
                    } else if (args[0].equalsIgnoreCase("help")) {
                        p.sendMessage("     §m               §r §eAide §7/ §f" + reportPl.getName() + " §m               \n"
                        + "§6/report help §f- §7Fenêtre d'aide du plugin ReportPlugin" + "\n"
                        + "§6/report list §f- §7Liste des rapports non traités." + "\n"
                        + "§6/report <joueur> <raison> §f- §7Rapporter un joueur." + "\n"
                        + "     §m               §r §eAide §7/ §f" + reportPl.getName() + " §m               ");
                    }
                    return true;
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        reportsList.add(new Report(target, p, String.join(" ", args).replace(args[0], ""), ""+new Date()));
                        reportPl.sendSuccess(p, "Rapport envoyé.");
                    } else {
                        reportPl.sendError(p, "Joueur inexistant.");
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method that creates the GUI for the reports list
     * @param p The player that will see the GUI
     */
    public void reportsListGui(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&3[Menu] &0Liste des rapports"));
        for (Report report : reportsList) {
            ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta im = (SkullMeta) i.getItemMeta();
            List<String> il = new ArrayList<String>();
            il.add(ChatColor.translateAlternateColorCodes('&',
                    "&7Joueur &8» &c" + report.getReportedPlayer().getName() +
                    "\n&7Raison &8» &c" + report.getReason().toString() +
                    "\n&7Date &8» &c" + report.getDateOfReport().toString() +
                    "\n&7Rapporté par &8» &c" + report.getReportingPlayer().getName() +
                    "\n\n&eClic Gauche &8» &7Traiter le rapport." +
                    "\n&eClic Droit &8» &7Se téléporter au joueur."));
            im.setOwner(report.getReportedPlayer().getName());
            im.setLore(il);
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRapport N°" + report.getId()));
            i.setItemMeta(im);
            inv.addItem(i);
        }
        p.openInventory(inv);
    }


    //endregion
}
