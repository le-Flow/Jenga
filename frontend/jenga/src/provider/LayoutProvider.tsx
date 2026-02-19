import { createContext, JSXElement } from "solid-js";

type LayoutContextType = {
}

const LayoutContext = createContext<LayoutContextType>();

interface LayoutProviderProps {
    children: JSXElement;
}

export const LayoutProvider = (props: LayoutProviderProps) => {

    const value: LayoutContextType = {

    };

    return (
        <LayoutContext.Provider value={value}>
            {props.children}
        </LayoutContext.Provider>
    )
}