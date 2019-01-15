package ow.routing.dlg;

import ow.routing.RoutingAlgorithm;
import ow.routing.RoutingAlgorithmConfiguration;
import ow.routing.RoutingAlgorithmProvider;
import ow.routing.RoutingService;

import java.security.InvalidAlgorithmParameterException;

/**
 * Created by muyuchen on 2018/11/15.
 */
public class DlgProvider implements RoutingAlgorithmProvider{
    private final static String ALGORITHM_NAME = "Dlg";

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    public RoutingAlgorithmConfiguration getDefaultConfiguration() {
        return new DlgConfiguration();
    }

    @Override
    public RoutingAlgorithm initializeAlgorithmInstance(RoutingAlgorithmConfiguration conf, RoutingService routingSvc) throws InvalidAlgorithmParameterException {
        return new Dlg(conf, routingSvc);
    }
}
