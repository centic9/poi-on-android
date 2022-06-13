/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.*;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.SystemCache;
import org.apache.xmlbeans.impl.common.XBeanDebug;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.*;

import static org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl.METADATA_PACKAGE_GEN;

/**
 * This class is to replace SchemaTypeLoaderImpl class within XMLBeans 5.1.0
 * that will crash in Android environment.
 * <p />
 * The following changes are applied to avoid crashes: <br/>
 * 1. build(): Load some classes manually as they cannot be loaded as "resource" on Android
 */
public class SchemaTypeLoaderImpl extends SchemaTypeLoaderBase {
    private final ResourceLoader _resourceLoader;
    private final ClassLoader _classLoader;
    private final SchemaTypeLoader[] _searchPath;

    private Map<String, SchemaTypeSystemImpl> _classpathTypeSystems;
    private Map<String, SchemaTypeSystemImpl> _classLoaderTypeSystems;
    private Map<QName, Object> _elementCache;
    private Map<QName, Object> _attributeCache;
    private Map<QName, Object> _modelGroupCache;
    private Map<QName, Object> _attributeGroupCache;
    private Map<QName, Object> _idConstraintCache;
    private Map<QName, Object> _typeCache;
    private Map<QName, Object> _documentCache;
    private Map<QName, Object> _attributeTypeCache;
    private Map<String, Object> _classnameCache;
    private final String _metadataPath;

    public static String METADATA_PACKAGE_LOAD = METADATA_PACKAGE_GEN;
    private static final Object CACHED_NOT_FOUND = new Object();

    private static final String[] basePackage = {"org.apache.xmlbeans.metadata", "schemaorg_apache_xmlbeans"};
    private static final String[] baseSchemas = {"sXMLCONFIG", "sXMLLANG", "sXMLSCHEMA", "sXMLTOOLS"};


    private static class SchemaTypeLoaderCache extends SystemCache {
        // The following maintains a cache of SchemaTypeLoaders per ClassLoader per Thread.
        // I use soft references to allow the garbage collector to reclaim the type loaders
        // and/or class loaders at will.

        private final ThreadLocal<List<SoftReference<SchemaTypeLoaderImpl>>> _cachedTypeSystems = ThreadLocal.withInitial(ArrayList::new);

        @Override
        public void clearThreadLocals() {
            _cachedTypeSystems.remove();

            super.clearThreadLocals();
        }

        public SchemaTypeLoader getFromTypeLoaderCache(ClassLoader cl) {
            List<SoftReference<SchemaTypeLoaderImpl>> a = _cachedTypeSystems.get();

            int candidate = -1;
            SchemaTypeLoaderImpl result = null;

            for (int i = 0; i < a.size(); i++) {
                SchemaTypeLoaderImpl tl = a.get(i).get();

                if (tl == null) {
                    a.remove(i--);
                } else if (tl._classLoader == cl) {
                    candidate = i;
                    result = tl;
                    break;
                }
            }

            // Make sure the most recently accessed entry is at the beginning of the array

            if (candidate > 0) {
                SoftReference<SchemaTypeLoaderImpl> t = a.get(0);
                a.set(0, a.get(candidate));
                a.set(candidate, t);
            }

            return result;
        }

        public void addToTypeLoaderCache(SchemaTypeLoader stl, ClassLoader cl) {
            assert (stl instanceof SchemaTypeLoaderImpl) &&
                   ((SchemaTypeLoaderImpl) stl)._classLoader == cl;

            List<SoftReference<SchemaTypeLoaderImpl>> a = _cachedTypeSystems.get();
            // Make sure this entry is at the top of the stack
            if (a.size() > 0) {
                SoftReference<SchemaTypeLoaderImpl> t = a.get(0);
                a.set(0, new SoftReference<>((SchemaTypeLoaderImpl) stl));
                a.add(t);
            } else {
                a.add(new SoftReference<>((SchemaTypeLoaderImpl) stl));
            }
        }
    }

    public static SchemaTypeLoaderImpl getContextTypeLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SchemaTypeLoaderImpl result = (SchemaTypeLoaderImpl)
            SystemCache.get().getFromTypeLoaderCache(cl);

        if (result == null) {
            result =
                new SchemaTypeLoaderImpl(
                    new SchemaTypeLoader[]{BuiltinSchemaTypeSystem.get()}, null, cl, null);
            SystemCache.get().addToTypeLoaderCache(result, cl);
        }

        return result;
    }

    public static SchemaTypeLoader build(SchemaTypeLoader[] searchPath, ResourceLoader resourceLoader, ClassLoader classLoader) {
        return build(searchPath, resourceLoader, classLoader, null);
    }

    /**
     * Initialize a SchemaTypeLoader via the given loaders and paths
     *
     * @param searchPath     the searchPath to use
     * @param resourceLoader the resourceLoader to use
     * @param classLoader    the classLoader to use
     * @param metadataPath   the custom metadata path
     * @return the schemaTypeLoader
     * @since XmlBeans 3.1.0
     */
    public static SchemaTypeLoader build(final SchemaTypeLoader[] searchPath, ResourceLoader resourceLoader, ClassLoader classLoader, String metadataPath) {
        // assemble a flattened search path with no duplicates
        SubLoaderList list = new SubLoaderList();

        list.add(searchPath);

        ClassLoader cl = (classLoader == null) ? SchemaDocument.class.getClassLoader() : classLoader;

        for (String prefix : basePackage) {
            for (String holder : baseSchemas) {
                String clName = prefix + ".system." + holder + ".TypeSystemHolder";

                // BEGIN CHANGES =========================================================================
                //
                // Simply remove this pre-check and handle ClassNotFoundException below
                // as Android does not find .class-files in getResource()
                //
                // See https://stackoverflow.com/questions/72616517/looking-up-class-files-via-classloader-getresource-on-android
                // for a related question
                //
                /*if (cl.getResource(clName.replace(".", "/") + ".class") == null) {
                    // if the first class isn't found in the package, continue with the next package
                    break;
                }*/

                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends SchemaTypeLoader> cls = (Class<? extends SchemaTypeLoader>) Class.forName(clName, true, cl);
                    list.add((SchemaTypeLoader) cls.getDeclaredField("typeSystem").get(null));
                } catch (ClassNotFoundException e) {
                    // if the first class isn't found in the package, continue with the next package
                    // this can happen and thus is ignored here
                } catch (Exception e) {
                    throw new XmlRuntimeException(e);
                }
                // END-CHANGES =========================================================================
            }
        }

        return new SchemaTypeLoaderImpl(list.toArray(), resourceLoader, classLoader, metadataPath);
    }

    /**
     * Just used to avoid duplicate path entries
     */
    private static class SubLoaderList {
        private final List<SchemaTypeLoader> theList = new ArrayList<>();
        private final Map<SchemaTypeLoader, Object> seen = new IdentityHashMap<>();

        void add(SchemaTypeLoader[] searchPath) {
            if (searchPath == null) {
                return;
            }
            for (SchemaTypeLoader stl : searchPath) {
                if (stl instanceof SchemaTypeLoaderImpl) {
                    SchemaTypeLoaderImpl sub = (SchemaTypeLoaderImpl) stl;
                    if (sub._classLoader != null || sub._resourceLoader != null) {
                        add(sub);
                    } else {
                        add(sub._searchPath);
                    }
                } else {
                    add(stl);
                }
            }
        }

        void add(SchemaTypeLoader loader) {
            if (loader != null && !seen.containsKey(loader)) {
                theList.add(loader);
                seen.put(loader, null);
            }
        }

        SchemaTypeLoader[] toArray() {
            return theList.toArray(EMPTY_SCHEMATYPELOADER_ARRAY);
        }
    }

    /**
     * Constructs a SchemaTypeLoaderImpl that searches for objects
     * in the following order:
     * <p>
     * (1) First on the searchPath of other SchemaTypeSystems supplied,
     * in order that they are listed.
     * (2) Next on the classpath of .jar files or directories supplied,
     * in the order that they are listed. When types are returned in
     * this way, they are instantiated from a private typesystem.
     * In other words, if a type is loaded from another SchemaTypeLoaderImpl
     * that was initialized on the same file, the instance of the type will
     * be different.
     * (3) Finally on the classloader supplied.
     */
    private SchemaTypeLoaderImpl(SchemaTypeLoader[] searchPath, ResourceLoader resourceLoader, ClassLoader classLoader, String metadataPath) {
        _searchPath = (searchPath == null) ? EMPTY_SCHEMATYPELOADER_ARRAY : searchPath;
        _resourceLoader = resourceLoader;
        _classLoader = classLoader;

        if (metadataPath != null) {
            this._metadataPath = metadataPath;
        } else {
            final String path26 = "schema" + METADATA_PACKAGE_LOAD.replace("/", "_");
            this._metadataPath = (isPath30(_classLoader)) ? METADATA_PACKAGE_LOAD : path26;
        }

        initCaches();
    }

    private static boolean isPath30(ClassLoader loader) {
        final String path30 = METADATA_PACKAGE_LOAD + "/system";
        final ClassLoader cl = (loader != null) ? loader : SchemaDocument.class.getClassLoader();
        return cl.getResource(path30) != null;
    }

    /**
     * Initializes the caches.
     */
    private void initCaches() {
        _classpathTypeSystems = Collections.synchronizedMap(new HashMap<>());
        _classLoaderTypeSystems = Collections.synchronizedMap(new HashMap<>());
        _elementCache = Collections.synchronizedMap(new HashMap<>());
        _attributeCache = Collections.synchronizedMap(new HashMap<>());
        _modelGroupCache = Collections.synchronizedMap(new HashMap<>());
        _attributeGroupCache = Collections.synchronizedMap(new HashMap<>());
        _idConstraintCache = Collections.synchronizedMap(new HashMap<>());
        _typeCache = Collections.synchronizedMap(new HashMap<>());
        _documentCache = Collections.synchronizedMap(new HashMap<>());
        _attributeTypeCache = Collections.synchronizedMap(new HashMap<>());
        _classnameCache = Collections.synchronizedMap(new HashMap<>());
    }

    SchemaTypeSystemImpl typeSystemForComponent(String searchdir, QName name) {
        String searchfor = searchdir + QNameHelper.hexsafedir(name) + ".xsb";
        String tsname = null;

        if (_resourceLoader != null) {
            tsname = crackEntry(_resourceLoader, searchfor);
        }

        if (_classLoader != null) {
            tsname = crackEntry(_classLoader, searchfor);
        }

        if (tsname != null) {
            return (SchemaTypeSystemImpl) typeSystemForName(tsname);
        }

        return null;
    }

    public SchemaTypeSystem typeSystemForName(String name) {
        if (_resourceLoader != null) {
            SchemaTypeSystem result = getTypeSystemOnClasspath(name);
            if (result != null) {
                return result;
            }
        }

        if (_classLoader != null) {
            SchemaTypeSystem result = getTypeSystemOnClassloader(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    SchemaTypeSystemImpl typeSystemForClassname(String searchdir, String name) {
        String searchfor = searchdir + name.replace('.', '/') + ".xsb";

        if (_resourceLoader != null) {
            String tsname = crackEntry(_resourceLoader, searchfor);
            if (tsname != null) {
                return getTypeSystemOnClasspath(tsname);
            }
        }

        if (_classLoader != null) {
            String tsname = crackEntry(_classLoader, searchfor);
            if (tsname != null) {
                return getTypeSystemOnClassloader(tsname);
            }
        }

        return null;
    }

    SchemaTypeSystemImpl getTypeSystemOnClasspath(String name) {
        return _classpathTypeSystems.computeIfAbsent(name, n -> new SchemaTypeSystemImpl(_resourceLoader, n, this));
    }

    SchemaTypeSystemImpl getTypeSystemOnClassloader(String name) {
        XBeanDebug.LOG.atTrace().log("Finding type system {} on classloader", name);
        SchemaTypeSystemImpl result = _classLoaderTypeSystems.get(name);
        if (result == null) {
            XBeanDebug.LOG.atTrace().log("Type system {}} not cached - consulting field", name);
            result = SchemaTypeSystemImpl.forName(name, _classLoader);
            _classLoaderTypeSystems.put(name, result);
        }
        return result;
    }

    static String crackEntry(ResourceLoader loader, String searchfor) {
        InputStream is = loader.getResourceAsStream(searchfor);
        return is == null ? null : crackPointer(is);
    }

    static String crackEntry(ClassLoader loader, String searchfor) {
        InputStream stream = loader.getResourceAsStream(searchfor);
        return stream == null ? null : crackPointer(stream);
    }

    static String crackPointer(InputStream stream) {
        return SchemaTypeSystemImpl.crackPointer(stream);
    }

    public boolean isNamespaceDefined(String namespace) {
        for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
            if (schemaTypeLoader.isNamespaceDefined(namespace)) {
                return true;
            }
        }

        SchemaTypeSystem sts = typeSystemForComponent(_metadataPath + "/namespace/", new QName(namespace, "xmlns"));
        return (sts != null);
    }

    public SchemaType.Ref findTypeRef(QName name) {
        // The maps are synchronized, we use two accesses to the cache (one read and one write), but the code in-between
        // is not synchronized. The assumption is that the underlying datastructures (the search path and the classloader)
        // do not change, so two threads running the code in parallel will come up with the same result.
        Object cached = _typeCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findTypeRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/type/", name);
                if (ts != null) {
                    result = ts.findTypeRef(name);
                    assert (result != null) : "Type system registered type " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            _typeCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaType typeForClassname(String classname) {
        classname = classname.replace('$', '.');

        Object cached = _classnameCache.get(classname);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType result = (SchemaType) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.typeForClassname(classname))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForClassname(_metadataPath + "/javaname/", classname);
                if (ts != null) {
                    result = ts.typeForClassname(classname);
                    assert (result != null) : "Type system registered type " + classname + " but does not return it";
                }
            }
            _classnameCache.put(classname, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaType.Ref findDocumentTypeRef(QName name) {
        Object cached = _documentCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findDocumentTypeRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/element/", name);
                if (ts != null) {
                    result = ts.findDocumentTypeRef(name);
                    assert (result != null) : "Type system registered element " + QNameHelper.pretty(name) + " but does not contain document type";
                }
            }
            _documentCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaType.Ref findAttributeTypeRef(QName name) {
        Object cached = _attributeTypeCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findAttributeTypeRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/attribute/", name);
                if (ts != null) {
                    result = ts.findAttributeTypeRef(name);
                    assert (result != null) : "Type system registered attribute " + QNameHelper.pretty(name) + " but does not contain attribute type";
                }
            }
            _attributeTypeCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaGlobalElement.Ref findElementRef(QName name) {
        Object cached = _elementCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaGlobalElement.Ref result = (SchemaGlobalElement.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findElementRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/element/", name);
                if (ts != null) {
                    result = ts.findElementRef(name);
                    assert (result != null) : "Type system registered element " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            _elementCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaGlobalAttribute.Ref findAttributeRef(QName name) {
        Object cached = _attributeCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaGlobalAttribute.Ref result = (SchemaGlobalAttribute.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findAttributeRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/attribute/", name);
                if (ts != null) {
                    result = ts.findAttributeRef(name);
                    assert (result != null) : "Type system registered attribute " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            _attributeCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaModelGroup.Ref findModelGroupRef(QName name) {
        Object cached = _modelGroupCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaModelGroup.Ref result = (SchemaModelGroup.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findModelGroupRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/modelgroup/", name);
                if (ts != null) {
                    result = ts.findModelGroupRef(name);
                    assert (result != null) : "Type system registered model group " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            _modelGroupCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaAttributeGroup.Ref findAttributeGroupRef(QName name) {
        Object cached = _attributeGroupCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaAttributeGroup.Ref result = (SchemaAttributeGroup.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findAttributeGroupRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/attributegroup/", name);
                if (ts != null) {
                    result = ts.findAttributeGroupRef(name);
                    assert (result != null) : "Type system registered attribute group " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            _attributeGroupCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(QName name) {
        Object cached = _idConstraintCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaIdentityConstraint.Ref result = (SchemaIdentityConstraint.Ref) cached;
        if (result == null) {
            for (SchemaTypeLoader schemaTypeLoader : _searchPath) {
                if (null != (result = schemaTypeLoader.findIdentityConstraintRef(name))) {
                    break;
                }
            }
            if (result == null) {
                SchemaTypeSystem ts = typeSystemForComponent(_metadataPath + "/identityconstraint/", name);
                if (ts != null) {
                    result = ts.findIdentityConstraintRef(name);
                    assert (result != null) : "Type system registered identity constraint " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            _idConstraintCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    public InputStream getSourceAsStream(String sourceName) {
        InputStream result = null;

        if (!sourceName.startsWith("/")) {
            sourceName = "/" + sourceName;
        }

        if (_resourceLoader != null) {
            result = _resourceLoader.getResourceAsStream(_metadataPath + "/src" + sourceName);
        }

        if (result == null && _classLoader != null) {
            return _classLoader.getResourceAsStream(_metadataPath + "/src" + sourceName);
        }

        return result;
    }

    private static final SchemaTypeLoader[] EMPTY_SCHEMATYPELOADER_ARRAY = new SchemaTypeLoader[0];

    static {
        SystemCache.set(new SchemaTypeLoaderCache());
    }
}
