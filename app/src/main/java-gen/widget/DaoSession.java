package widget;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig widgetIDDaoConfig;

    private final WidgetIDDao widgetIDDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        widgetIDDaoConfig = daoConfigMap.get(WidgetIDDao.class).clone();
        widgetIDDaoConfig.initIdentityScope(type);

        widgetIDDao = new WidgetIDDao(widgetIDDaoConfig, this);

        registerDao(WidgetID.class, widgetIDDao);
    }
    
    public void clear() {
        widgetIDDaoConfig.getIdentityScope().clear();
    }

    public WidgetIDDao getWidgetIDDao() {
        return widgetIDDao;
    }

}
