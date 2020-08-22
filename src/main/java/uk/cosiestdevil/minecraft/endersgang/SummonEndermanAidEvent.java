package uk.cosiestdevil.minecraft.endersgang;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.eventbus.api.Event;

/**
 * SummonAidEvent is fired when a Zombie Entity is summoned.
 * This event is fired whenever a Zombie Entity is summoned in
 * {@link EntityZombie#attackEntityFrom(DamageSource, float)}.
 *
 * This event is fired via the {@link ForgeEventFactory#fireZombieSummonAid(EntityZombie, World, int, int, int, EntityLivingBase, double)}.
 *
 * {@link #customSummonedAid} remains null, but can be populated with a custom EntityZombie which will be spawned.
 * {@link #world} contains the world that this summoning is occurring in.
 * {@link #x} contains the x-coordinate at which this summoning event is occurring.
 * {@link #y} contains the y-coordinate at which this summoning event is occurring.
 * {@link #z} contains the z-coordinate at which this summoning event is occurring.
 * {@link #attacker} contains the living Entity that attacked and caused this event to fire.
 * {@link #summonChance} contains the likelihood that a Zombie would successfully be summoned.
 *
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
 *
 * This event has a result. {@link Event.HasResult}
 * {@link Event.Result#ALLOW} Zombie is summoned.
 * {@link Event.Result#DENY} Zombie is not summoned.
 *
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Event.HasResult
public class SummonEndermanAidEvent extends EntityEvent {
    public EndermanLeader getSummoner()
    {
        return (EndermanLeader) getEntity();
    }
    private EndermanEntity customSummonedAid;

    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final LivingEntity attacker;
    private final double summonChance;

    public SummonEndermanAidEvent(EndermanLeader entity, World world, int x, int y, int z, LivingEntity attacker, double summonChance)
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
     * Populate this field to have a custom zombie instead of a normal zombie summoned
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