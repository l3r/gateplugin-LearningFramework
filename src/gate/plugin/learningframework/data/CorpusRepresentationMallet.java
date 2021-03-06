/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package gate.plugin.learningframework.data;

import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import gate.AnnotationSet;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

/**
 * Common base class for Mallet for classification and  Mallet for sequence tagging.
 * @author Johann Petrak
 */
public abstract class CorpusRepresentationMallet extends CorpusRepresentation {

  Logger logger = org.apache.log4j.Logger.getLogger(CorpusRepresentationMallet.class);

  protected InstanceList instances;

  public InstanceList getRepresentationMallet() { return instances; }
  
  public Object getRepresentation() { return instances; }
  
  public LFPipe getPipe() {
    if(instances == null) return null;
    if(instances.getPipe() == null) {
      return null;
    } else {
      return (LFPipe)instances.getPipe();
    }
  }
  
  /**
   * Prevent the addition of new features or feature values when instances are added.
   */
  public void stopGrowth() {
    LFPipe pipe = (LFPipe)instances.getPipe();
    pipe.getDataAlphabet().stopGrowth();
    Alphabet ta = pipe.getTargetAlphabet();
    if(ta != null) ta.stopGrowth();
    FeatureInfo fi = pipe.getFeatureInfo();
    fi.stopGrowth();
  }
  
  /**
   * Enable the addition of new features or feature values when instances are added.
   * After a CorpusRepresentationMallet instance is created, growth is enabled by default.
   */
  public void startGrowth() {
    LFPipe pipe = (LFPipe)instances.getPipe();
    pipe.getDataAlphabet().startGrowth();
    Alphabet ta = pipe.getTargetAlphabet();
    if(ta != null) ta.startGrowth();
    FeatureInfo fi = pipe.getFeatureInfo();
    fi.startGrowth();    
  }
    
  public void savePipe(File directory) {
    File outFile = new File(directory,"pipe.pipe");
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(outFile));
      oos.writeObject(pipe);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not save LFPipe for CorpusRepresentationMallet to "+outFile,ex);
    } finally {
      if(oos!=null) try {
        oos.close();
      } catch (IOException ex) {
        logger.error("Could not close stream after saving LFPipe to "+outFile, ex);
      }        
    }
  }
  
  public abstract void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String instanceWeightFeature, String nameFeatureName);
  
  /**
   * Finish adding data to the CR. This will do any re-scaling and other outstanding calculations
   * on the whole corpus. 
   * @param scaleFeatures 
   */
  public abstract void finish();
  
  // TODO: need to do this better: make sure if there are thousands of 
  // features that we only show a subset and the number?
  /*
  public String toString() {
    Alphabet dataAlph = pipe.getDataAlphabet();
    Alphabet targetAlph = pipe.getTargetAlphabet();
    StringBuilder sb = new StringBuilder();
    sb.append("CorpusRepresentationMallet{dataalphabet=");
    sb.append(dataAlph.toString());
    sb.append(",targetalphabet=");
    if(targetAlph==null) {
      sb.append("null");
    } else {
      sb.append(targetAlph.toString());
    }
    sb.append("}");
    return sb.toString();
  }
  */
  
}
