package net.minecraft.entity.item;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer, ILootContainer {
   private ItemStack[] field_94113_a = new ItemStack[36];
   private boolean field_94112_b = true;
   private ResourceLocation field_184290_c;
   private long field_184291_d;

   public EntityMinecartContainer(World p_i1716_1_) {
      super(p_i1716_1_);
   }

   public EntityMinecartContainer(World p_i1717_1_, double p_i1717_2_, double p_i1717_4_, double p_i1717_6_) {
      super(p_i1717_1_, p_i1717_2_, p_i1717_4_, p_i1717_6_);
   }

   public void func_94095_a(DamageSource p_94095_1_) {
      super.func_94095_a(p_94095_1_);
      if(this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         InventoryHelper.func_180176_a(this.field_70170_p, this, this);
      }

   }

   @Nullable
   public ItemStack func_70301_a(int p_70301_1_) {
      this.func_184288_f((EntityPlayer)null);
      return this.field_94113_a[p_70301_1_];
   }

   @Nullable
   public ItemStack func_70298_a(int p_70298_1_, int p_70298_2_) {
      this.func_184288_f((EntityPlayer)null);
      return ItemStackHelper.func_188382_a(this.field_94113_a, p_70298_1_, p_70298_2_);
   }

   @Nullable
   public ItemStack func_70304_b(int p_70304_1_) {
      this.func_184288_f((EntityPlayer)null);
      if(this.field_94113_a[p_70304_1_] != null) {
         ItemStack itemstack = this.field_94113_a[p_70304_1_];
         this.field_94113_a[p_70304_1_] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   public void func_70299_a(int p_70299_1_, @Nullable ItemStack p_70299_2_) {
      this.func_184288_f((EntityPlayer)null);
      this.field_94113_a[p_70299_1_] = p_70299_2_;
      if(p_70299_2_ != null && p_70299_2_.field_77994_a > this.func_70297_j_()) {
         p_70299_2_.field_77994_a = this.func_70297_j_();
      }

   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(EntityPlayer p_70300_1_) {
      return this.field_70128_L?false:p_70300_1_.func_70068_e(this) <= 64.0D;
   }

   public void func_174889_b(EntityPlayer p_174889_1_) {
   }

   public void func_174886_c(EntityPlayer p_174886_1_) {
   }

   public boolean func_94041_b(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   public int func_70297_j_() {
      return 64;
   }

   @Nullable
   public Entity func_184204_a(int p_184204_1_) {
      this.field_94112_b = false;
      return super.func_184204_a(p_184204_1_);
   }

   public void func_70106_y() {
      if(this.field_94112_b) {
         InventoryHelper.func_180176_a(this.field_70170_p, this, this);
      }

      super.func_70106_y();
   }

   public void func_184174_b(boolean p_184174_1_) {
      this.field_94112_b = p_184174_1_;
   }

   protected void func_70014_b(NBTTagCompound p_70014_1_) {
      super.func_70014_b(p_70014_1_);
      if(this.field_184290_c != null) {
         p_70014_1_.func_74778_a("LootTable", this.field_184290_c.toString());
         if(this.field_184291_d != 0L) {
            p_70014_1_.func_74772_a("LootTableSeed", this.field_184291_d);
         }
      } else {
         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 0; i < this.field_94113_a.length; ++i) {
            if(this.field_94113_a[i] != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.func_74774_a("Slot", (byte)i);
               this.field_94113_a[i].func_77955_b(nbttagcompound);
               nbttaglist.func_74742_a(nbttagcompound);
            }
         }

         p_70014_1_.func_74782_a("Items", nbttaglist);
      }

   }

   protected void func_70037_a(NBTTagCompound p_70037_1_) {
      super.func_70037_a(p_70037_1_);
      this.field_94113_a = new ItemStack[this.func_70302_i_()];
      if(p_70037_1_.func_150297_b("LootTable", 8)) {
         this.field_184290_c = new ResourceLocation(p_70037_1_.func_74779_i("LootTable"));
         this.field_184291_d = p_70037_1_.func_74763_f("LootTableSeed");
      } else {
         NBTTagList nbttaglist = p_70037_1_.func_150295_c("Items", 10);

         for(int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
            int j = nbttagcompound.func_74771_c("Slot") & 255;
            if(j >= 0 && j < this.field_94113_a.length) {
               this.field_94113_a[j] = ItemStack.func_77949_a(nbttagcompound);
            }
         }
      }

   }

   public boolean func_184230_a(EntityPlayer p_184230_1_, @Nullable ItemStack p_184230_2_, EnumHand p_184230_3_) {
      if(!this.field_70170_p.field_72995_K) {
         p_184230_1_.func_71007_a(this);
      }

      return true;
   }

   protected void func_94101_h() {
      float f = 0.98F;
      if(this.field_184290_c == null) {
         int i = 15 - Container.func_94526_b(this);
         f += (float)i * 0.001F;
      }

      this.field_70159_w *= (double)f;
      this.field_70181_x *= 0.0D;
      this.field_70179_y *= (double)f;
   }

   public int func_174887_a_(int p_174887_1_) {
      return 0;
   }

   public void func_174885_b(int p_174885_1_, int p_174885_2_) {
   }

   public int func_174890_g() {
      return 0;
   }

   public boolean func_174893_q_() {
      return false;
   }

   public void func_174892_a(LockCode p_174892_1_) {
   }

   public LockCode func_174891_i() {
      return LockCode.field_180162_a;
   }

   public void func_184288_f(@Nullable EntityPlayer p_184288_1_) {
      if(this.field_184290_c != null) {
         LootTable loottable = this.field_70170_p.func_184146_ak().func_186521_a(this.field_184290_c);
         this.field_184290_c = null;
         Random random;
         if(this.field_184291_d == 0L) {
            random = new Random();
         } else {
            random = new Random(this.field_184291_d);
         }

         LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)this.field_70170_p);
         if(p_184288_1_ != null) {
            lootcontext$builder.func_186469_a(p_184288_1_.func_184817_da());
         }

         loottable.func_186460_a(this, random, lootcontext$builder.func_186471_a());
      }

   }

   public void func_174888_l() {
      this.func_184288_f((EntityPlayer)null);

      for(int i = 0; i < this.field_94113_a.length; ++i) {
         this.field_94113_a[i] = null;
      }

   }

   public void func_184289_a(ResourceLocation p_184289_1_, long p_184289_2_) {
      this.field_184290_c = p_184289_1_;
      this.field_184291_d = p_184289_2_;
   }

   public ResourceLocation func_184276_b() {
      return this.field_184290_c;
   }
}
