/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.versionchecker.util;

import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class VersionHelper implements IVersionHelper {

  public String getVersionInformation() {
    return getVersionInformation( VersionHelper.class );
  }

  @SuppressWarnings( "rawtypes" )
  public String getVersionInformation( Class clazz ) {
    // The following two lines read from the MANIFEST.MF
    String implTitle = clazz.getPackage().getImplementationTitle();
    String implVersion = clazz.getPackage().getImplementationVersion();
    if ( implVersion != null ) {
      // If we're in a .jar file, then we can return the version information
      // from the .jar file.
      return implTitle + " " + implVersion; //$NON-NLS-1$
    } else {
      // We're not in a .jar file - try to find the version file and
      // read the version information from that.
      try {
        ResourceBundle bundle = ResourceBundle.getBundle( "version" ); //$NON-NLS-1$
        StringBuffer buff = new StringBuffer();
        buff.append( bundle.getString( "impl.title" ) ).append( ' ' ).append( bundle.getString( "release.major.number" ) ).append( '.' ).append( bundle.getString( "release.minor.number" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buff.append( '.' )
            .append( bundle.getString( "release.milestone.number" ) ).append( '.' ).append( bundle.getString( "release.build.number" ) ).append( " (class)" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return buff.toString();
      } catch ( Exception ex ) {
        return "Pentaho BI Platform - No Version Information Available"; //$NON-NLS-1$
      }
    }
  }

  public static VersionInfo getVersionInfo() {
    return getVersionInfo( VersionHelper.class );
  }

  @SuppressWarnings( "rawtypes" )
  public static VersionInfo getVersionInfo( Class clazz ) {
    // Try to get the manifest for the class
    final Manifest manifest = ManifestUtil.getManifest( clazz );
    if ( manifest != null ) {
      // Return the version info from the manifest
      return createVersionInfo( manifest );
    }
    // Return the default version info (from properties file)
    return createVersionInfo();
  }

  /**
   * Extracts the version information data from the manifest's attributes and puts them into a VersionInfo instance.
   * 
   * @param manifest
   *          the manifest information
   * @return the version information from the manifest
   */
  protected static VersionInfo createVersionInfo( Manifest manifest ) {
    final VersionInfo versionInfo = new VersionInfo();
    final Attributes mainAttributes = manifest.getMainAttributes();
    versionInfo.setFromManifest( true );
    versionInfo.setProductID( mainAttributes.getValue( "Implementation-ProductID" ) ); //$NON-NLS-1$
    versionInfo.setTitle( mainAttributes.getValue( "Implementation-Title" ) ); //$NON-NLS-1$
    versionInfo.setVersion( mainAttributes.getValue( "Implementation-Version" ) ); //$NON-NLS-1$
    return versionInfo;
  }

  /**
   * Extracts the version information data from the <code>version.properties</code> file found in the source
   * directory.
   * 
   * @return the version information from the <code>version.properties</code> file
   */
  protected static VersionInfo createVersionInfo() {
    // We're not in a .jar file - try to find the version file and
    // read the version information from that.
    final VersionInfo versionInfo = new VersionInfo();
    try {
      final ResourceBundle bundle = ResourceBundle.getBundle( "version" ); //$NON-NLS-1$
      versionInfo.setFromManifest( false );
      versionInfo.setProductID( bundle.getString( "impl.productID" ) ); //$NON-NLS-1$
      versionInfo.setTitle( bundle.getString( "impl.title" ) ); //$NON-NLS-1$
      versionInfo.setVersionMajor( bundle.getString( "release.major.number" ) ); //$NON-NLS-1$
      versionInfo.setVersionMinor( bundle.getString( "release.minor.number" ) ); //$NON-NLS-1$
      versionInfo.setVersionBuild( bundle.getString( "release.build.number" ) ); //$NON-NLS-1$

      // The release milestone number has both the release number and the milestone number
      final String releaseMilestoneNumber = bundle.getString( "release.milestone.number" ); //$NON-NLS-1$
      if ( releaseMilestoneNumber != null ) {
        String[] parts = releaseMilestoneNumber.replace( '-', '.' ).split( "\\." ); //$NON-NLS-1$
        if ( parts.length > 0 ) {
          versionInfo.setVersionRelease( parts[0] );
          if ( parts.length > 1 ) {
            versionInfo.setVersionMilestone( parts[1] );
          }
        }
      }
    } catch ( Exception e ) {
      // TODO:log
      versionInfo.setVersion( "No Version Information Available" ); //$NON-NLS-1$
    }
    return versionInfo;
  }
}
