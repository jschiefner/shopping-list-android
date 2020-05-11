package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import com.jschiefner.shoppinglist.sync.SyncJob;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RuleRepository {
    private RuleDao ruleDao;
    private CategoryDao categoryDao;
    private LiveData<List<Rule>> categoryRules;

    public RuleRepository(Application application, long categoryId) {
        ItemDatabase database = ItemDatabase.getInstance(application);
        ruleDao = database.ruleDao();
        categoryDao = database.categoryDao();
        categoryRules = ruleDao.getCategoryRules(categoryId);
    }

    public void insert(Rule rule) {
        new InsertRuleTask(ruleDao, categoryDao).execute(rule);
        SyncJob.getInstance().perform();
    }

    public void delete(Rule rule) {
        new DeleteRuleTask(ruleDao).execute(rule);
        SyncJob.getInstance().delete(rule).perform();
    }

    public void getRuleWithCategory(String name, QueryHandler handler) {
        new QueryRuleWithCategoryTask(ruleDao, name, handler).execute();
    }

    public LiveData<List<Rule>> getCategoryRules() {
        return categoryRules;
    }

    private static class InsertRuleTask extends AsyncTask<Rule, Void, Void> {
        private RuleDao ruleDao;
        private CategoryDao categoryDao;

        private InsertRuleTask(RuleDao ruleDao, CategoryDao categoryDao) {
            this.ruleDao = ruleDao;
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Rule... rules) {
            Rule rule = rules[0];
            Category category = categoryDao.getCategory(rule.categoryId);
            SyncJob.getInstance().create(rule, category.uuid);
            ruleDao.insert(rule);
            return null;
        }
    }

    private static class DeleteRuleTask extends AsyncTask<Rule, Void, Void> {
        private RuleDao ruleDao;

        private DeleteRuleTask(RuleDao ruleDao) {
            this.ruleDao = ruleDao;
        }

        @Override
        protected Void doInBackground(Rule... rules) {
            ruleDao.delete(rules[0]);
            return null;
        }
    }

    private static class QueryRuleWithCategoryTask extends AsyncTask<Void, Void, Void> {
        private RuleDao ruleDao;
        private String name;
        private QueryHandler handler;
        private RuleWithCategory ruleWithCategory;

        private QueryRuleWithCategoryTask(RuleDao ruleDao, String name, QueryHandler handler) {
            this.ruleDao = ruleDao;
            this.name = name;
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ruleWithCategory = ruleDao.getRuleWithCategory(name);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            handler.handle(ruleWithCategory);
        }
    }
}
