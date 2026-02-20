import { createContext, JSXElement, useContext } from "solid-js";

import 'shepherd.js/dist/css/shepherd.css';
import Shepherd from "shepherd.js";


type GuideContextType = {
    startGuide: () => void;
    stopGuide: () => void;
}

interface GuideProviderProps {
    children: JSXElement;
}

export const GuideContext = createContext<GuideContextType>();

export const GuideProvider = (props: GuideProviderProps) => {

    const steps = [
        {
            id: 'welcome',
            text: 'Welcome to Jenga! This tour will guide you through the main features of the application.',
        }
    ];

    const tour = new Shepherd.Tour({
        defaultStepOptions: {
            classes: 'shepherd-theme-arrows',
            scrollTo: true,
            buttons: [
                {
                    text: 'X',
                    action: function () {
                        return this.cancel();
                    }
                },
                {
                    text: '<',
                    action: function () {
                        return this.back();

                    }
                },
                {
                    text: '>',
                    action: function () {
                        return this.next();
                    }
                },
            ]
        }
    });
    tour.addSteps(steps);

    const startGuide = () => {
        tour.start();
    }

    const stopGuide = () => {
        tour.cancel();
    }

    const value: GuideContextType = {
        startGuide,
        stopGuide,
    }

    return (
        <GuideContext.Provider value={value}>
            {props.children}
        </GuideContext.Provider>
    )
}
