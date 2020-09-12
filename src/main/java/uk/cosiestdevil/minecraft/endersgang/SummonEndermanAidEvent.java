package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class SummonEndermanAidEvent extends EntityEvent {
    public EndermanLeaderEntity getSummoner()
    {
        return (EndermanLeaderEntity) getEntity();
    }
    private EndermanEntity customSummonedAid;

    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final LivingEntity attacker;
    private final double summonChance;

    public SummonEndermanAidEvent(EndermanLeaderEntity entity, World world, int x, int y, int z, LivingEntity attacker, double summonChance)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.attacker = attacker;
        this.summonChance = summonChance;
    }

    /**
     * Populate this field to have a custom enderman instead of a normal enderman summoned
     */
    public EndermanEntity getCustomSummonedAid() { return customSummonedAid; }
    public void setCustomSummonedAid(EndermanEntity customSummonedAid) { this.customSummonedAid = customSummonedAid; }
    public World getWorld() { return world; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public LivingEntity getAttacker() { return attacker; }
    public double getSummonChance() { return summonChance; }
}
