package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
   private static final Logger field_177320_c = LogManager.getLogger();
   private static final FileFilter field_110622_a = new FileFilter() {
      public boolean accept(File p_accept_1_) {
         boolean flag = p_accept_1_.isFile() && p_accept_1_.getName().endsWith(".zip");
         boolean flag1 = p_accept_1_.isDirectory() && (new File(p_accept_1_, "pack.mcmeta")).isFile();
         return flag || flag1;
      }
   };
   private final File field_110618_d;
   public final IResourcePack field_110620_b;
   private final File field_148534_e;
   public final MetadataSerializer field_110621_c;
   private IResourcePack field_148532_f;
   private final ReentrantLock field_177321_h = new ReentrantLock();
   private ListenableFuture<Object> field_177322_i;
   private List<ResourcePackRepository.Entry> field_110619_e = Lists.<ResourcePackRepository.Entry>newArrayList();
   private List<ResourcePackRepository.Entry> field_110617_f = Lists.<ResourcePackRepository.Entry>newArrayList();

   public ResourcePackRepository(File p_i45101_1_, File p_i45101_2_, IResourcePack p_i45101_3_, MetadataSerializer p_i45101_4_, GameSettings p_i45101_5_) {
      this.field_110618_d = p_i45101_1_;
      this.field_148534_e = p_i45101_2_;
      this.field_110620_b = p_i45101_3_;
      this.field_110621_c = p_i45101_4_;
      this.func_110616_f();
      this.func_110611_a();
      Iterator<String> iterator = p_i45101_5_.field_151453_l.iterator();

      while(iterator.hasNext()) {
         String s = (String)iterator.next();

         for(ResourcePackRepository.Entry resourcepackrepository$entry : this.field_110619_e) {
            if(resourcepackrepository$entry.func_110515_d().equals(s)) {
               if(resourcepackrepository$entry.func_183027_f() == 2 || p_i45101_5_.field_183018_l.contains(resourcepackrepository$entry.func_110515_d())) {
                  this.field_110617_f.add(resourcepackrepository$entry);
                  break;
               }

               iterator.remove();
               field_177320_c.warn("Removed selected resource pack {} because it\'s no longer compatible", new Object[]{resourcepackrepository$entry.func_110515_d()});
            }
         }
      }

   }

   private void func_110616_f() {
      if(this.field_110618_d.exists()) {
         if(!this.field_110618_d.isDirectory() && (!this.field_110618_d.delete() || !this.field_110618_d.mkdirs())) {
            field_177320_c.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.field_110618_d);
         }
      } else if(!this.field_110618_d.mkdirs()) {
         field_177320_c.warn("Unable to create resourcepack folder: " + this.field_110618_d);
      }

   }

   private List<File> func_110614_g() {
      return this.field_110618_d.isDirectory()?Arrays.asList(this.field_110618_d.listFiles(field_110622_a)):Collections.emptyList();
   }

   public void func_110611_a() {
      List<ResourcePackRepository.Entry> list = Lists.<ResourcePackRepository.Entry>newArrayList();

      for(File file1 : this.func_110614_g()) {
         ResourcePackRepository.Entry resourcepackrepository$entry = new ResourcePackRepository.Entry(file1);
         if(!this.field_110619_e.contains(resourcepackrepository$entry)) {
            try {
               resourcepackrepository$entry.func_110516_a();
               list.add(resourcepackrepository$entry);
            } catch (Exception var6) {
               list.remove(resourcepackrepository$entry);
            }
         } else {
            int i = this.field_110619_e.indexOf(resourcepackrepository$entry);
            if(i > -1 && i < this.field_110619_e.size()) {
               list.add(this.field_110619_e.get(i));
            }
         }
      }

      this.field_110619_e.removeAll(list);

      for(ResourcePackRepository.Entry resourcepackrepository$entry1 : this.field_110619_e) {
         resourcepackrepository$entry1.func_110517_b();
      }

      this.field_110619_e = list;
   }

   @Nullable
   public ResourcePackRepository.Entry func_188565_b() {
      if(this.field_148532_f != null) {
         ResourcePackRepository.Entry resourcepackrepository$entry = new ResourcePackRepository.Entry(this.field_148532_f);

         try {
            resourcepackrepository$entry.func_110516_a();
            return resourcepackrepository$entry;
         } catch (IOException var3) {
            ;
         }
      }

      return null;
   }

   public List<ResourcePackRepository.Entry> func_110609_b() {
      return ImmutableList.copyOf(this.field_110619_e);
   }

   public List<ResourcePackRepository.Entry> func_110613_c() {
      return ImmutableList.copyOf(this.field_110617_f);
   }

   public void func_148527_a(List<ResourcePackRepository.Entry> p_148527_1_) {
      this.field_110617_f.clear();
      this.field_110617_f.addAll(p_148527_1_);
   }

   public File func_110612_e() {
      return this.field_110618_d;
   }

   public ListenableFuture<Object> func_180601_a(String p_180601_1_, String p_180601_2_) {
      String s = DigestUtils.sha1Hex(p_180601_1_);
      String s1 = p_180601_2_.matches("^[a-f0-9]{40}$")?p_180601_2_:"";
      final File file1 = new File(this.field_148534_e, s);
      this.field_177321_h.lock();

      try {
         this.func_148529_f();
         if(file1.exists()) {
            try {
               String s2 = DigestUtils.sha1Hex((InputStream)(new FileInputStream(file1)));
               if(!s1.equals("") && s2.equals(s1)) {
                  field_177320_c.info("Found file " + file1 + " matching requested hash " + s1);
                  ListenableFuture listenablefuture2 = this.func_177319_a(file1);
                  return listenablefuture2;
               }

               if(!s1.equals("") && !s2.equals(s1)) {
                  field_177320_c.warn("File " + file1 + " had wrong hash (expected " + s1 + ", found " + s2 + "). Deleting it.");
                  FileUtils.deleteQuietly(file1);
               } else if(s1.equals("")) {
                  field_177320_c.info("Found file " + file1 + " without verification hash");
                  ListenableFuture lvt_7_1_ = this.func_177319_a(file1);
                  return lvt_7_1_;
               }
            } catch (IOException ioexception) {
               field_177320_c.warn((String)("File " + file1 + " couldn\'t be hashed. Deleting it."), (Throwable)ioexception);
               FileUtils.deleteQuietly(file1);
            }
         }

         this.func_183028_i();
         final GuiScreenWorking guiscreenworking = new GuiScreenWorking();
         Map<String, String> map = Minecraft.func_175596_ai();
         final Minecraft minecraft = Minecraft.func_71410_x();
         Futures.getUnchecked(minecraft.func_152344_a(new Runnable() {
            public void run() {
               minecraft.func_147108_a(guiscreenworking);
            }
         }));
         final SettableFuture<Object> settablefuture = SettableFuture.<Object>create();
         this.field_177322_i = HttpUtil.func_180192_a(file1, p_180601_1_, map, 52428800, guiscreenworking, minecraft.func_110437_J());
         Futures.addCallback(this.field_177322_i, new FutureCallback<Object>() {
            public void onSuccess(@Nullable Object p_onSuccess_1_) {
               ResourcePackRepository.this.func_177319_a(file1);
               settablefuture.set((Object)null);
            }

            public void onFailure(Throwable p_onFailure_1_) {
               settablefuture.setException(p_onFailure_1_);
            }
         });
         ListenableFuture listenablefuture1 = this.field_177322_i;
         return listenablefuture1;
      } finally {
         this.field_177321_h.unlock();
      }
   }

   private void func_183028_i() {
      try {
         List<File> list = Lists.newArrayList(FileUtils.listFiles(this.field_148534_e, TrueFileFilter.TRUE, (IOFileFilter)null));
         Collections.sort(list, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int i = 0;

         for(File file1 : list) {
            if(i++ >= 10) {
               field_177320_c.info("Deleting old server resource pack " + file1.getName());
               FileUtils.deleteQuietly(file1);
            }
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         field_177320_c.error("Error while deleting old server resource pack : " + illegalargumentexception.getMessage());
      }

   }

   public ListenableFuture<Object> func_177319_a(File p_177319_1_) {
      this.field_148532_f = new FileResourcePack(p_177319_1_);
      return Minecraft.func_71410_x().func_175603_A();
   }

   public IResourcePack func_148530_e() {
      return this.field_148532_f;
   }

   public void func_148529_f() {
      this.field_177321_h.lock();

      try {
         if(this.field_177322_i != null) {
            this.field_177322_i.cancel(true);
         }

         this.field_177322_i = null;
         if(this.field_148532_f != null) {
            this.field_148532_f = null;
            Minecraft.func_71410_x().func_175603_A();
         }
      } finally {
         this.field_177321_h.unlock();
      }

   }

   public class Entry {
      private IResourcePack field_110524_c;
      private PackMetadataSection field_110521_d;
      private ResourceLocation field_110520_f;

      private Entry(File p_i1295_2_) {
         this((IResourcePack)(p_i1295_2_.isDirectory()?new FolderResourcePack(p_i1295_2_):new FileResourcePack(p_i1295_2_)));
      }

      private Entry(IResourcePack p_i46558_2_) {
         this.field_110524_c = p_i46558_2_;
      }

      public void func_110516_a() throws IOException {
         this.field_110521_d = (PackMetadataSection)this.field_110524_c.func_135058_a(ResourcePackRepository.this.field_110621_c, "pack");
         this.func_110517_b();
      }

      public void func_110518_a(TextureManager p_110518_1_) {
         BufferedImage bufferedimage = null;

         try {
            bufferedimage = this.field_110524_c.func_110586_a();
         } catch (IOException var5) {
            ;
         }

         if(bufferedimage == null) {
            try {
               bufferedimage = ResourcePackRepository.this.field_110620_b.func_110586_a();
            } catch (IOException ioexception) {
               throw new Error("Couldn\'t bind resource pack icon", ioexception);
            }
         }

         if(this.field_110520_f == null) {
            this.field_110520_f = p_110518_1_.func_110578_a("texturepackicon", new DynamicTexture(bufferedimage));
         }

         p_110518_1_.func_110577_a(this.field_110520_f);
      }

      public void func_110517_b() {
         if(this.field_110524_c instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)this.field_110524_c);
         }

      }

      public IResourcePack func_110514_c() {
         return this.field_110524_c;
      }

      public String func_110515_d() {
         return this.field_110524_c.func_130077_b();
      }

      public String func_110519_e() {
         return this.field_110521_d == null?TextFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)":this.field_110521_d.func_152805_a().func_150254_d();
      }

      public int func_183027_f() {
         return this.field_110521_d == null?0:this.field_110521_d.func_110462_b();
      }

      public boolean equals(Object p_equals_1_) {
         return this == p_equals_1_?true:(p_equals_1_ instanceof ResourcePackRepository.Entry?this.toString().equals(p_equals_1_.toString()):false);
      }

      public int hashCode() {
         return this.toString().hashCode();
      }

      public String toString() {
         return String.format("%s:%s", new Object[]{this.field_110524_c.func_130077_b(), this.field_110524_c instanceof FolderResourcePack?"folder":"zip"});
      }
   }
}
