package net.slqmy.tss_core.datatype.player.survival;

import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.event.custom_event.SkillLevelUpEvent;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SurvivalPlayerData {

  @BsonIgnore
  private UUID playerUuid;

  private Map<String, ArrayList<Claim>> claims = new HashMap<>();
  private Map<String, Integer> skillData = new HashMap<>();

  public SurvivalPlayerData() {

  }

  public SurvivalPlayerData(UUID playerUuid, @NotNull TSSCorePlugin plugin) {
    this.playerUuid = playerUuid;

    initialiseClaims(plugin);
    initialiseSkills();
  }

  public Map<String, ArrayList<Claim>> getClaims() {
    return claims;
  }

  public Map<String, Integer> getSkillData() {
    return skillData;
  }

  public void setPlayerUuid(UUID playerUuid) {
    this.playerUuid = playerUuid;
  }

  public void setClaims(Map<String, ArrayList<Claim>> claims) {
    this.claims = claims;
  }

  public void setSkillData(Map<String, Integer> skillData) {
    this.skillData = skillData;
  }

  public void incrementSkillExperience(@NotNull SkillType skillType, int increaseAmount) {
    int currentExp = skillData.get(skillType.name());
    int oldLevel = getLevel(currentExp);

    int newExp = currentExp + increaseAmount;
    skillData.put(skillType.name(), newExp);

    int newLevel = getLevel(newExp);
    if (oldLevel != newLevel) {
      Bukkit.getPluginManager().callEvent(
              new SkillLevelUpEvent(
                      Bukkit.getPlayer(playerUuid),
                      skillType,
                      oldLevel,
                      newLevel
              )
      );
    }
  }

  private int getLevel(int experience) {
    double power = Math.pow(Math.sqrt(3D) * Math.sqrt(27D * Math.pow(experience, 2D) + 10000D) - 9D * experience, 1D / 3D);
    return (int) Math.floor(Math.pow(10D, 2D / 3D) / (Math.pow(3D, 1D / 3D) * power) - power / Math.pow(30D, 2D / 3D));
  }

  public void initialiseClaims(@NotNull TSSCorePlugin plugin) {
    for (String worldName : plugin.getSurvivalWorldNames()) {
      claims.put(worldName, new ArrayList<>());
    }
  }

  public void initialiseSkills() {
    for (SkillType skillType : SkillType.values()) {
      skillData.put(skillType.name(), 0);
    }
  }
}
