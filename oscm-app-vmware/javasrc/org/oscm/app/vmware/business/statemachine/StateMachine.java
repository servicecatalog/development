package org.oscm.app.vmware.business.statemachine;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.vmware.business.statemachine.api.StateMachineException;
import org.oscm.app.vmware.business.statemachine.api.StateMachineProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachine {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    private States states;

    private String stateId;

    private String machine;

    private String history;

    public StateMachine(ProvisioningSettings settings)
            throws StateMachineException {
        machine = settings.getParameters()
                .get(StateMachineProperties.SM_STATE_MACHINE);
        states = loadStateMachine(machine);
        history = settings.getParameters()
                .get(StateMachineProperties.SM_STATE_HISTORY);
        stateId = settings.getParameters().get(StateMachineProperties.SM_STATE);
    }

    private States loadStateMachine(String filename)
            throws StateMachineException {
        logger.debug("filename: " + filename);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader
                .getResourceAsStream("statemachines/" + filename);) {
            JAXBContext jaxbContext = JAXBContext.newInstance(States.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (States) jaxbUnmarshaller.unmarshal(stream);
        } catch (Exception e) {
            throw new StateMachineException(
                    "Failed to load state machine definition file: " + filename,
                    e);
        }
    }

    public String getStateId() {
        return stateId;
    }

    public String getHistory() {
        return history;
    }

    public static void initializeProvisioningSettings(
            ProvisioningSettings settings, String stateMachine) {
        settings.getParameters().put(StateMachineProperties.SM_STATE_HISTORY,
                "");
        settings.getParameters().put(StateMachineProperties.SM_STATE_MACHINE,
                stateMachine);
        settings.getParameters().put(StateMachineProperties.SM_STATE, "BEGIN");
    }

    public String executeAction(ProvisioningSettings settings,
            String instanceId, InstanceStatus result)
                    throws StateMachineException {

        State state = getState(stateId);
        String eventId = states.invokeAction(state, instanceId, settings,
                result);
        history = appendStateToHistory(stateId, history);
        stateId = getNextState(state, eventId);
        return stateId;
    }

    private State getState(String stateId) throws StateMachineException {
        for (State state : states.getStates()) {
            if (state.getId().equals(stateId)) {
                return state;
            }
        }
        throw new StateMachineException("State " + stateId + " not found");
    }

    private String getNextState(State state, String eventId)
            throws StateMachineException {

        for (Event event : state.getEvents()) {
            if (event.getId().equals(eventId)) {
                logger.debug("current state: " + state.getId() + " event: " + eventId + " next state: " + event.getState());
                return event.getState();
            }
        }

        throw new StateMachineException(
                "No next state defined for current state: " + state.getId() + " event: " + eventId);
    }

    String appendStateToHistory(String state, String stateHistory) {
        if (stateHistory == null || stateHistory.trim().length() == 0) {
            return state;
        } else if (!stateHistory.endsWith(state)) {
            return stateHistory.concat(",").concat(state);
        }
        return stateHistory;
    }

    public String loadPreviousStateFromHistory(ProvisioningSettings settings)
            throws StateMachineException {

        String currentState = settings.getParameters()
                .get(StateMachineProperties.SM_STATE);
        String stateHistory = settings.getParameters()
                .get(StateMachineProperties.SM_STATE_HISTORY);
        String[] states = stateHistory.split(",");
        for (int i = states.length - 1; i >= 0; i--) {
            if (!states[i].equals(currentState)) {
                return states[i];
            }
        }

        throw new StateMachineException(
                "Couldn't find a previous state for statemachine " + machine
                        + " and current state " + stateId);
    }
}
