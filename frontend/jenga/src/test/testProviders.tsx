import { JSXElement, createSignal } from "solid-js";
import { AuthContext } from "../provider/AuthProvider";
import { I18nContext } from "../provider/I18nProvider";
import { ProjectContext } from "../provider/ProjectProvider";

type OverrideMap = {
    auth?: Record<string, unknown>;
    i18n?: Record<string, unknown>;
    project?: Record<string, unknown>;
};

type TestWrapperProps = {
    children: JSXElement;
};

const makeResource = <T,>(getter: () => T) => {
    const resource = (() => getter()) as unknown as { (): T; error?: unknown };
    resource.error = undefined;
    return resource;
};

export const createAuthContextMock = (overrides?: Record<string, unknown>) => {
    const authMock = {
        login: async () => { },
        isLoggedIn: () => false,
        register: async () => { },
        jwt: () => undefined,
        loginError: () => undefined,
        registerError: () => undefined,
        loginLoading: () => false,
        registerLoading: () => false,
        logout: () => { },
    };

    return { ...authMock, ...(overrides ?? {}) };
};

export const createI18nContextMock = (overrides?: Record<string, unknown>) => {
    const i18nMock = {
        t: (key: string) => key,
        language: () => "en",
        changeLanguage: async () => { },
    };

    return { ...i18nMock, ...(overrides ?? {}) };
};

export const createProjectContextMock = (overrides?: Record<string, unknown>) => {
    const [selectedProject, setSelectedProject] = createSignal<{ identifier: string; name?: string } | undefined>();
    const [selectedTicket, setSelectedTicket] = createSignal<{ id?: number } | undefined>();
    const [tickets, setTickets] = createSignal<unknown[] | undefined>([]);
    const [projects, setProjects] = createSignal<unknown[] | undefined>([]);
    const [availableLabels, setAvailableLabels] = createSignal<string[]>([]);

    const projectMock = {
        projects: makeResource(() => projects()),
        setProjects,
        refetchProjects: async () => projects(),
        selectedProject,
        setSelectedProject,
        tickets: makeResource(() => tickets()),
        setTickets,
        refetchTickets: async () => tickets(),
        refetchAll: async () => { },
        selectedTicket,
        setSelectedTicket,
        deleteProject: async () => { },
        updateTicket: async () => { },
        createLabel: async (labelName: string) => labelName,
        deleteLabel: async () => { },
        availableLabels: makeResource(() => availableLabels()),
        setAvailableLabels,
    };

    return { ...projectMock, ...(overrides ?? {}) };
};

export const withTestProviders = (ui: JSXElement, overrides?: OverrideMap) => {
    const Wrapper = createTestWrapper(overrides);
    return <Wrapper>{ui}</Wrapper>;
};

export const createTestWrapper = (overrides?: OverrideMap) => {
    const authValue = createAuthContextMock(overrides?.auth);
    const i18nValue = createI18nContextMock(overrides?.i18n);
    const projectValue = createProjectContextMock(overrides?.project);

    return (props: TestWrapperProps) => (
        <I18nContext.Provider value={i18nValue as never}>
            <AuthContext.Provider value={authValue as never}>
                <ProjectContext.Provider value={projectValue as never}>
                    {props.children}
                </ProjectContext.Provider>
            </AuthContext.Provider>
        </I18nContext.Provider>
    );
};
