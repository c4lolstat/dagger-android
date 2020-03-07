package com.chronic.poc.di;

import com.chronic.poc.viewmodels.ViewModelProviderFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

/** @Module informs Dagger that this class is a Dagger Module
 * */
@Module
public abstract class ViewModelFactoryModule {

    //@Bind interface to implementation
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);

}
