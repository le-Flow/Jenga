import { Accessor, createContext, createSignal, JSXElement, Setter } from "solid-js";

type LayoutContextType = {
    sidebarOpen: Accessor<boolean>;
    setSidebarOpen: Setter<boolean>;
    toggleSidebar: () => void;
}

export const LayoutContext = createContext<LayoutContextType>();

interface LayoutProviderProps {
    children: JSXElement;
}

export const LayoutProvider = (props: LayoutProviderProps) => {
    const [sidebarOpen, setSidebarOpen] = createSignal(false);

    const value: LayoutContextType = {
        sidebarOpen,
        setSidebarOpen,
        toggleSidebar: () => setSidebarOpen((prev) => !prev),
    };

    return (
        <LayoutContext.Provider value={value}>
            {props.children}
        </LayoutContext.Provider>
    )
}
