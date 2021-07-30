package st.netb.mc.hsrails;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class MinecartListener implements Listener {

    private static final double BUKKIT_SPEED_MULTIPLIER = 0.4d;

    private final HsRails plugin;
    private final Material boostBlock;

    public MinecartListener(HsRails plugin, Material boostBlock) {
        this.plugin = plugin;
        this.boostBlock = boostBlock;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleExit(VehicleExitEvent event) {

        if (event.getExited() instanceof Player && event.getVehicle() instanceof Minecart) {
            Player player = (Player) event.getExited();
            Minecart cart = (Minecart) event.getVehicle();

            new BukkitRunnable() {

                public void run() {

                    if (player.isOnline() && cart.isEmpty() && cart.getMaxSpeed() > BUKKIT_SPEED_MULTIPLIER) {
                        Location cartLocation = cart.getLocation();

                        cart.remove();
                        cart.getWorld().dropItemNaturally(cartLocation, new ItemStack(Material.MINECART));
                    }
                }
            }.runTask(plugin);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (event.getVehicle() instanceof Minecart) {
            Minecart cart = (Minecart) event.getVehicle();
            Location cartLocation = cart.getLocation();
            World cartsWorld = cart.getWorld();

            Block rail = cartsWorld.getBlockAt(cartLocation);
            Block blockBelow = cartsWorld.getBlockAt(cartLocation.add(0, -1, 0));

            if (rail.getType() == Material.POWERED_RAIL) {
                if (blockBelow.getType() == boostBlock) {
                    cart.setMaxSpeed(BUKKIT_SPEED_MULTIPLIER * HsRails.getConfiguration().getSpeedMultiplier());
                }
                else {
                    cart.setMaxSpeed(BUKKIT_SPEED_MULTIPLIER);
                }
            }
        }
    }
}
